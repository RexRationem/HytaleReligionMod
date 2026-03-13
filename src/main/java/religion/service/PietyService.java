package religion.service;

import religion.config.ModConfig;
import religion.data.DoctrineType;
import religion.data.Religion;

public class PietyService {

    private final ModConfig config;
    private final EventLogService eventLog;

    public PietyService(ModConfig config, EventLogService eventLog) {
        this.config = config;
        this.eventLog = eventLog;
    }

    public void recomputeCap(Religion religion, long now) {
        int baseCap = config.basePietyCap + (religion.getLaymanCount() * config.pietyPerLayman);
        int relicBonusCap = religion.relics.size() * config.relicSacrificeBonusFlat;

        religion.pietyCap = baseCap + relicBonusCap;

        if (religion.piety > religion.pietyCap) {
            religion.piety = religion.pietyCap;
        }

        eventLog.add(religion, now, "PIETY_CAP_RECALCULATED", "New cap=" + religion.pietyCap);
    }

    public void addPiety(Religion religion, int amount, long now, String reason) {
        if (amount <= 0) {
            return;
        }

        if (religion.cursed && now < religion.curseEndsAtEpochMillis) {
            eventLog.add(religion, now, "PIETY_FROZEN", "Blocked gain during curse, reason=" + reason);
            return;
        }

        religion.piety = Math.min(religion.pietyCap, religion.piety + amount);
        eventLog.add(religion, now, "PIETY_GAIN", reason + " +" + amount + " => " + religion.piety);
    }

    public void removePiety(Religion religion, int amount, long now, String reason) {
        if (amount <= 0) {
            return;
        }

        int adjusted = amount;

        if (reason != null
                && reason.contains("HOLY_WAR")
                && religion.doctrines.contains(DoctrineType.REDUCED_HOLY_WAR_PIETY_LOSS)) {
            adjusted = (int) Math.round(adjusted * 0.75);
        }

        religion.piety = Math.max(0, religion.piety - adjusted);

        eventLog.add(religion, now, "PIETY_LOSS", reason + " -" + adjusted + " => " + religion.piety);
    }

    public boolean spendPiety(Religion religion, int amount, long now, String reason) {
        if (amount <= 0) {
            return true;
        }

        if (religion.cursed && now < religion.curseEndsAtEpochMillis) {
            eventLog.add(religion, now, "PIETY_SPEND_BLOCKED", "Blocked spend during curse, reason=" + reason);
            return false;
        }

        if (religion.piety < amount) {
            eventLog.add(
                    religion,
                    now,
                    "PIETY_SPEND_FAILED",
                    reason + ", required=" + amount + ", current=" + religion.piety
            );
            return false;
        }

        religion.piety -= amount;
        eventLog.add(religion, now, "PIETY_SPENT", reason + " -" + amount + " => " + religion.piety);
        return true;
    }

    public void applyCurse(Religion religion, long now, String reason) {
        religion.cursed = true;

        long baseDurationMillis = config.curseFreezeHours * 60L * 60L * 1000L;
        if (religion.doctrines.contains(DoctrineType.FASTER_CURSE_RECOVERY)) {
            baseDurationMillis = Math.max(60_000L, Math.round(baseDurationMillis * 0.75));
        }

        religion.curseEndsAtEpochMillis = now + baseDurationMillis;

        eventLog.add(
                religion,
                now,
                "CURSE_APPLIED",
                reason + ", endsAt=" + religion.curseEndsAtEpochMillis
        );
    }

    public void clearCurseIfExpired(Religion religion, long now) {
        if (!religion.cursed) {
            return;
        }

        if (now >= religion.curseEndsAtEpochMillis) {
            religion.cursed = false;
            religion.curseEndsAtEpochMillis = 0L;
            eventLog.add(religion, now, "CURSE_EXPIRED", "Curse expired naturally");
        }
    }
}