package religion.data;

import religion.api.BlockPos;

import java.util.UUID;

public class RelicRecord {
    public UUID relicId;
    public String itemId;
    public String sourceReligionName;
    public RelicState state;
    public UUID carrierPlayerId;
    public BlockPos location;
}