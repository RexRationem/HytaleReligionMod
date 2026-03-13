package religion.util;

public final class TimeUtil {
    private TimeUtil() {}

    public static long hoursToMillis(int hours) {
        return hours * 60L * 60L * 1000L;
    }

    public static long minutesToMillis(int minutes) {
        return minutes * 60L * 1000L;
    }

    public static long daysToMillis(int days) {
        return days * 24L * 60L * 60L * 1000L;
    }
}