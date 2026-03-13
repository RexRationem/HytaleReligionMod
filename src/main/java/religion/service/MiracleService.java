package religion.service;

import religion.api.PlayerAdapter;
import religion.api.ServerBridge;
import religion.config.ModConfig;
import religion.data.DoctrineType;
import religion.data.Religion;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MiracleService {

    private final ModConfig config;
    private final PietyService pietyService;
    private final EventLogService eventLog;
    private final ServerBridge bridge;
    private final Random random = new Random();

    public MiracleService(
            ModConfig config,
            PietyService pietyService,
            EventLogService eventLog,
            ServerBridge bridge
    ) {
        this.config = config;
        this.pietyService = pietyService;
        this.eventLog = eventLog;
        this.bridge = bridge;
    }

    public boolean canUseMiracles(Religion religion) {
        return religion != null && religion.mainAltarPos != null && religion.hasLayman();
    }

    public boolean attemptMiracle(Religion religion, long now) {
        if (!canUseMiracles(religion)) {
            eventLog.add(religion, now, "MIRACLE_BLOCKED", "Missing main altar or layman");
            return false;
        }

        int cost = config.miracleCost;
        if (religion.doctrines.contains(DoctrineType.LOWER_MIRACLE_COST)) {
            cost = Math.max(1, (int) Math.round(cost * 0.8));
        }

        if (religion.freeHolidayMiracleRolls > 0) {
            religion.freeHolidayMiracleRolls--;
            eventLog.add(religion, now, "MIRACLE_FREE_ROLL_USED", "Holiday roll consumed");
        } else {
            if (!pietyService.spendPiety(religion, cost, now, "MIRACLE_PRAYER")) {
                return false;
            }
        }

        double chance = Math.min(config.miracleChanceCapPercent, religion.pietyCap / 100.0);
        boolean success = (random.nextDouble() * 100.0) < chance;

        eventLog.add(religion, now, "MIRACLE_ROLL", "chance=" + chance + ", success=" + success);

        if (!success) {
            return false;
        }

        if (random.nextBoolean()) {
            triggerItemMiracle(religion, now);
        } else {
            triggerBuffMiracle(religion, now);
        }

        bridge.broadcast("[Religion] A miracle has blessed " + religion.name + "!");
        return true;
    }

    private void triggerItemMiracle(Religion religion, long now) {
        if (config.miracleItemPool == null || config.miracleItemPool.isEmpty()) {
            eventLog.add(religion, now, "ITEM_MIRACLE_SKIPPED", "No configured miracle items");
            return;
        }

        String itemId = config.miracleItemPool.get(random.nextInt(config.miracleItemPool.size()));
        int amount = religion.doctrines.contains(DoctrineType.BETTER_ITEM_MIRACLES) ? 2 : 1;

        List<String> recipients = new ArrayList<>();

        for (PlayerAdapter player : bridge.getOnlinePlayers()) {
            if (religion.members.containsKey(player.getUniqueId())) {
                player.giveItem(itemId, amount);
                recipients.add(player.getName());
            }
        }

        eventLog.add(
                religion,
                now,
                "ITEM_MIRACLE",
                "Item=" + itemId + ", amount=" + amount + ", recipients=" + recipients
        );
    }

    private void triggerBuffMiracle(Religion religion, long now) {
        if (config.miracleBuffPool == null || config.miracleBuffPool.isEmpty()) {
            eventLog.add(religion, now, "BUFF_MIRACLE_SKIPPED", "No configured miracle buffs");
            return;
        }

        String buffId = config.miracleBuffPool.get(random.nextInt(config.miracleBuffPool.size()));
        int durationSeconds = 10 * 60;
        int amplifier = religion.doctrines.contains(DoctrineType.STRONGER_BUFF_MIRACLES) ? 2 : 1;

        List<String> recipients = new ArrayList<>();

        for (PlayerAdapter player : bridge.getOnlinePlayers()) {
            if (religion.members.containsKey(player.getUniqueId())) {
                player.applyBuff(buffId, durationSeconds, amplifier);
                recipients.add(player.getName());
            }
        }

        eventLog.add(
                religion,
                now,
                "BUFF_MIRACLE",
                "Buff=" + buffId + ", durationSeconds=" + durationSeconds + ", amplifier=" + amplifier
                        + ", recipients=" + recipients
        );
    }
}