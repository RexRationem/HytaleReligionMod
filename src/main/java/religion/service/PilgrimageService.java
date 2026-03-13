package religion.service;

import religion.config.ModConfig;
import religion.data.Religion;

import java.util.*;

public class PilgrimageService {

    private static class ActivePilgrimage {
        String religionName;
        long startedAt;
        Set<String> visitedAltars = new HashSet<>();
    }

    private final ModConfig config;
    private final CooldownService cooldowns;
    private final PietyService pietyService;
    private final EventLogService eventLog;
    private final Map<UUID, ActivePilgrimage> active = new HashMap<>();

    public PilgrimageService(ModConfig config,
                             CooldownService cooldowns,
                             PietyService pietyService,
                             EventLogService eventLog) {
        this.config = config;
        this.cooldowns = cooldowns;
        this.pietyService = pietyService;
        this.eventLog = eventLog;
    }

    public boolean start(UUID playerId, Religion religion, String altarKey, long now) {
        String cooldownKey = "pilgrimage:" + playerId;
        if (cooldowns.isOnCooldown(cooldownKey, now)) {
            return false;
        }

        ActivePilgrimage p = new ActivePilgrimage();
        p.religionName = religion.name;
        p.startedAt = now;
        p.visitedAltars.add(altarKey);
        active.put(playerId, p);

        eventLog.add(religion, now, "PILGRIMAGE_STARTED", "Player=" + playerId + ", altar=" + altarKey);
        return true;
    }

    public boolean visit(UUID playerId, Religion religion, String altarKey, long now) {
        ActivePilgrimage p = active.get(playerId);
        if (p == null || !p.religionName.equals(religion.name)) {
            return false;
        }

        long maxMillis = config.pilgrimageTimeWindowMinutes * 60L * 1000L;
        if (now > p.startedAt + maxMillis) {
            active.remove(playerId);
            eventLog.add(religion, now, "PILGRIMAGE_FAILED", "Timed out for player=" + playerId);
            return false;
        }

        p.visitedAltars.add(altarKey);

        if (p.visitedAltars.size() >= religion.altars.size()) {
            pietyService.addPiety(religion, config.pilgrimageRewardPiety, now, "PILGRIMAGE_COMPLETE");
            cooldowns.setCooldown("pilgrimage:" + playerId, now, config.pilgrimageCooldownDays * 24L * 60L * 60L * 1000L);
            active.remove(playerId);
            eventLog.add(religion, now, "PILGRIMAGE_COMPLETED", "Player=" + playerId);
            return true;
        }

        return false;
    }
}