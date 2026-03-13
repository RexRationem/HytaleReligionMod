package religion.data;

public class ReligionEvent {
    public long timestampEpochMillis;
    public String religionName;
    public String type;
    public String details;

    public ReligionEvent(long timestampEpochMillis, String religionName, String type, String details) {
        this.timestampEpochMillis = timestampEpochMillis;
        this.religionName = religionName;
        this.type = type;
        this.details = details;
    }
}