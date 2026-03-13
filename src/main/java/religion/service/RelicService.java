package religion.service;

import religion.config.ModConfig;
import religion.data.RelicRecord;
import religion.data.RelicState;
import religion.data.Religion;

import java.util.UUID;

public class RelicService {

    private final ModConfig config;
    private final PietyService pietyService;
    private final EventLogService eventLog;

    public RelicService(ModConfig config, PietyService pietyService, EventLogService eventLog) {
        this.config = config;
        this.pietyService = pietyService;
        this.eventLog = eventLog;
    }

    public RelicRecord consecrate(Religion religion, String itemId, long now) {
        if (religion == null || itemId == null || itemId.isBlank()) {
            return null;
        }

        RelicRecord relic = new RelicRecord();
        relic.relicId = UUID.randomUUID();
        relic.itemId = itemId;
        relic.sourceReligionName = religion.name;
        relic.state = RelicState.SECURED_IN_ALTAR;
        relic.carrierPlayerId = null;
        relic.location = religion.mainAltarPos;

        religion.relics.add(relic);

        pietyService.recomputeCap(religion, now);
        eventLog.add(religion, now, "RELIC_CONSECRATED", "Item=" + itemId + ", relicId=" + relic.relicId);

        return relic;
    }

    public void markCarried(Religion religion, RelicRecord relic, UUID carrierId, long now) {
        if (religion == null || relic == null) {
            return;
        }

        relic.state = RelicState.CARRIED;
        relic.carrierPlayerId = carrierId;
        relic.location = null;

        eventLog.add(religion, now, "RELIC_CARRIED", "Relic=" + relic.relicId + ", carrier=" + carrierId);
    }

    public void markDropped(Religion religion, RelicRecord relic, long now) {
        if (religion == null || relic == null) {
            return;
        }

        relic.state = RelicState.DROPPED;
        relic.carrierPlayerId = null;

        eventLog.add(religion, now, "RELIC_DROPPED", "Relic=" + relic.relicId);
    }

    public void markSecuredInAltar(Religion religion, RelicRecord relic, long now) {
        if (religion == null || relic == null) {
            return;
        }

        relic.state = RelicState.SECURED_IN_ALTAR;
        relic.carrierPlayerId = null;
        relic.location = religion.mainAltarPos;

        eventLog.add(religion, now, "RELIC_SECURED_ALTAR", "Relic=" + relic.relicId);
    }

    public void markStolenEnshrined(Religion sourceReligion, Religion thiefReligion, RelicRecord relic, long now) {
        if (sourceReligion == null || thiefReligion == null || relic == null) {
            return;
        }

        relic.state = RelicState.STOLEN_ENSHRINED;
        relic.carrierPlayerId = null;

        eventLog.add(
                sourceReligion,
                now,
                "RELIC_STOLEN",
                "Relic=" + relic.relicId + ", thiefReligion=" + thiefReligion.name
        );
        eventLog.add(
                thiefReligion,
                now,
                "STOLEN_RELIC_ENSHRINED",
                "Relic=" + relic.relicId + ", from=" + sourceReligion.name
        );
    }

    public void processDailyStolenRelicTick(Religion sourceReligion, Religion thiefReligion, RelicRecord relic, long now) {
        if (sourceReligion == null || thiefReligion == null || relic == null) {
            return;
        }

        if (relic.state != RelicState.STOLEN_ENSHRINED) {
            return;
        }

        pietyService.removePiety(sourceReligion, config.stolenRelicDailyDrainPiety, now, "STOLEN_RELIC_DRAIN");
        pietyService.addPiety(thiefReligion, config.stolenRelicDailyGainPiety, now, "STOLEN_RELIC_GAIN");
    }
}