package religion.service;

import religion.api.ServerBridge;
import religion.config.ModConfig;
import religion.data.Religion;
import religion.data.Role;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HolyWarService {

    public static class HolyWar {
        public String attackerReligion;
        public String targetReligion;
        public long startsAt;
        public long endsAt;
        public int attackerKills;
        public int defenderKills;
    }

    private final ModConfig config;
    private final PietyService pietyService;
    private final CooldownService cooldownService;
    private final EventLogService eventLog;
    private final ServerBridge bridge;

    private final Map<String, HolyWar> activeWarsByAttacker = new HashMap<>();

    public HolyWarService(
            ModConfig config,
            PietyService pietyService,
            CooldownService cooldownService,
            EventLogService eventLog,
            ServerBridge bridge
    ) {
        this.config = config;
        this.pietyService = pietyService;
        this.cooldownService = cooldownService;
        this.eventLog = eventLog;
        this.bridge = bridge;
    }

    public boolean declareHolyWar(Religion attacker, Religion target, long now) {
        if (attacker == null || target == null) {
            return false;
        }

        if (attacker.name.equalsIgnoreCase(target.name)) {
            return false;
        }

        if (!pietyService.spendPiety(attacker, config.holyWarDeclarationCost, now, "HOLY_WAR_DECLARE")) {
            return false;
        }

        HolyWar war = new HolyWar();
        war.attackerReligion = attacker.name;
        war.targetReligion = target.name;
        war.startsAt = now;
        war.endsAt = now + (config.holyWarDurationHours * 60L * 60L * 1000L);

        activeWarsByAttacker.put(attacker.name, war);

        eventLog.add(attacker, now, "HOLY_WAR_DECLARED", "Target=" + target.name + ", endsAt=" + war.endsAt);
        eventLog.add(target, now, "HOLY_WAR_TARGETED", "By=" + attacker.name + ", endsAt=" + war.endsAt);

        bridge.broadcast("[Religion] " + attacker.name + " has declared Holy War on " + target.name + "!");
        return true;
    }

    public HolyWar getWarByAttacker(String attackerReligionName) {
        return activeWarsByAttacker.get(attackerReligionName);
    }

    public void cleanupExpired(long now) {
        activeWarsByAttacker.values().removeIf(war -> war.endsAt <= now);
    }

    public void handleKill(Religion killerReligion, Role victimRole, UUID killerId, UUID victimId, long now) {
        if (killerReligion == null || victimRole == null || killerId == null || victimId == null) {
            return;
        }

        HolyWar war = activeWarsByAttacker.get(killerReligion.name);
        if (war == null || war.endsAt <= now) {
            return;
        }

        String repeatKey = "holywar-kill:" + killerId + ":" + victimId;
        long repeatMillis = config.holyWarRepeatKillCooldownMinutes * 60L * 1000L;

        if (cooldownService.isOnCooldown(repeatKey, now)) {
            return;
        }

        cooldownService.setCooldown(repeatKey, now, repeatMillis);

        int value = getKillValue(victimRole);

        pietyService.addPiety(killerReligion, value, now, "HOLY_WAR_KILL");
        war.attackerKills++;

        eventLog.add(killerReligion, now, "HOLY_WAR_KILL", "VictimRole=" + victimRole + ", piety +" + value);
    }

    public void handleDeathPenalty(Religion victimReligion, Role victimRole, long now) {
        if (victimReligion == null || victimRole == null) {
            return;
        }

        int loss = getKillValue(victimRole);

        pietyService.removePiety(victimReligion, loss, now, "HOLY_WAR_DEATH");
        eventLog.add(victimReligion, now, "HOLY_WAR_DEATH", "Role=" + victimRole + ", piety -" + loss);

        if (victimReligion.piety == 0 && !victimReligion.cursed) {
            pietyService.applyCurse(victimReligion, now, "HOLY_WAR_ZERO_PIETY");
            bridge.broadcast("[Religion] " + victimReligion.name + " has been cursed!");
        }
    }

    private int getKillValue(Role role) {
        return switch (role) {
            case PROPHET -> 500;
            case PRIEST -> 100;
            case LAYMAN -> 50;
        };
    }
}