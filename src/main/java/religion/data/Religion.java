package religion.data;

import religion.api.BlockPos;

import java.util.*;

public class Religion {
    public String name;
    public UUID prophetId;

    public String symbol = "*";
    public String color = "#FFFFFF";
    public String description = "";
    public String holyText = "";
    public boolean isPublic = true;
    public String holidayWeekday = "SUNDAY";
    public long foundedAtEpochMillis;

    public int piety = 0;
    public int pietyCap = 100;
    public boolean cursed = false;
    public long curseEndsAtEpochMillis = 0L;

    public BlockPos mainAltarPos;
    public final List<BlockPos> altars = new ArrayList<>();
    public final List<BlockPos> shrines = new ArrayList<>();

    public final Map<UUID, MemberRecord> members = new HashMap<>();
    public final Set<String> encouragedItems = new HashSet<>();
    public final Set<String> bannedItems = new HashSet<>();
    public final List<DoctrineType> doctrines = new ArrayList<>();
    public final List<RelicRecord> relics = new ArrayList<>();
    public final List<ReligionEvent> history = new ArrayList<>();

    public int mainAltarUpgradeTier = 0;
    public int freeHolidayMiracleRolls = 0;

    public Religion() {}

    public Religion(String name, UUID prophetId, long foundedAtEpochMillis) {
        this.name = name;
        this.prophetId = prophetId;
        this.foundedAtEpochMillis = foundedAtEpochMillis;
        this.members.put(prophetId, new MemberRecord(prophetId, Role.PROPHET, foundedAtEpochMillis));
    }

    public int getLaymanCount() {
        int count = 0;
        for (MemberRecord member : members.values()) {
            if (member.role == Role.LAYMAN) {
                count++;
            }
        }
        return count;
    }

    public int getPriestCount() {
        int count = 0;
        for (MemberRecord member : members.values()) {
            if (member.role == Role.PRIEST) {
                count++;
            }
        }
        return count;
    }

    public boolean hasLayman() {
        return getLaymanCount() > 0;
    }

    public Role getRole(UUID playerId) {
        MemberRecord member = members.get(playerId);
        return member == null ? null : member.role;
    }
}