package religion.service;

import religion.config.ModConfig;
import religion.data.DoctrineType;
import religion.data.Religion;

import java.util.UUID;

public class ConfessionService {

    private final ModConfig config;
    private final CooldownService cooldownService;
    private final PietyService pietyService;
    private final EventLogService eventLog;

    public ConfessionService(ModConfig config,
                             CooldownService cooldownService,
                             PietyService pietyService,
                             EventLogService eventLog) {

        this.config = config;
        this.cooldownService = cooldownService;
        this.pietyService = pietyService;
        this.eventLog = eventLog;
    }

    public boolean confess(Religion religion, UUID playerId, long now, boolean encouragedItem, boolean holidayPerfect) {

        String key = "confession:" + playerId;
        long cooldown = config.confessionCooldownHours * 3600000L;

        if (cooldownService.isOnCooldown(key, now)) {
            return false;
        }

        int gain = config.confessionPietyGain;

        if (encouragedItem) {
            gain = applyBonus(gain, config.encouragedItemMaxBonusPercent);
        }

        if (religion.doctrines.contains(DoctrineType.INCREASED_CONFESSION_PIETY)) {
            gain = applyBonus(gain, 25.0);
        }

        pietyService.addPiety(religion, gain, now, "CONFESSION");

        cooldownService.setCooldown(key, now, cooldown);

        if (holidayPerfect) {
            religion.freeHolidayMiracleRolls++;
        }

        eventLog.add(religion, now, "CONFESSION", "Player=" + playerId + " +" + gain);

        return true;
    }

    private int applyBonus(int base, double percent) {
        return (int)Math.round(base * (1.0 + percent / 100.0));
    }
}