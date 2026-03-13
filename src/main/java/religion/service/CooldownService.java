package religion.service;

import java.util.HashMap;
import java.util.Map;

public class CooldownService {

    private final Map<String, Long> cooldowns = new HashMap<>();

    public boolean isOnCooldown(String key, long now) {
        Long until = cooldowns.get(key);
        return until != null && now < until;
    }

    public long getRemainingMillis(String key, long now) {
        Long until = cooldowns.get(key);
        if (until == null) return 0L;
        return Math.max(0L, until - now);
    }

    public void setCooldown(String key, long now, long durationMillis) {
        cooldowns.put(key, now + durationMillis);
    }
}