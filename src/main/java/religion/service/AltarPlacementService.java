package religion.service;

import religion.api.BlockPos;
import religion.config.ModConfig;
import religion.data.Religion;

public class AltarPlacementService {

    private final ModConfig config;

    public AltarPlacementService(ModConfig config) {
        this.config = config;
    }

    public boolean isValidAdditionalAltarPlacement(Religion religion, BlockPos newPos) {
        if (religion == null || newPos == null) {
            return false;
        }

        if (religion.mainAltarPos == null) {
            return true;
        }

        double distance = config.altarDistanceIsCenterToCenter
                ? newPos.chunkCenterDistanceTo(religion.mainAltarPos)
                : newPos.distanceTo(religion.mainAltarPos);

        return distance >= config.altarMinChunkDistance;
    }

    public boolean isValidShrinePlacement(Religion religion, BlockPos newPos) {
        if (religion == null || newPos == null) {
            return false;
        }

        for (BlockPos altar : religion.altars) {
            double distance = config.altarDistanceIsCenterToCenter
                    ? newPos.chunkCenterDistanceTo(altar)
                    : newPos.distanceTo(altar);

            if (distance < config.altarMinChunkDistance) {
                return false;
            }
        }

        for (BlockPos shrine : religion.shrines) {
            double distance = config.altarDistanceIsCenterToCenter
                    ? newPos.chunkCenterDistanceTo(shrine)
                    : newPos.distanceTo(shrine);

            if (distance < config.altarMinChunkDistance) {
                return false;
            }
        }

        return true;
    }
}