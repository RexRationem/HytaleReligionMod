package religion.data;

import java.util.UUID;

public class MemberRecord {
    public UUID playerId;
    public Role role;
    public long joinedAtEpochMillis;
    public long lastKnownPlaytimeMinutes;

    public MemberRecord() {}

    public MemberRecord(UUID playerId, Role role, long joinedAtEpochMillis) {
        this.playerId = playerId;
        this.role = role;
        this.joinedAtEpochMillis = joinedAtEpochMillis;
    }
}