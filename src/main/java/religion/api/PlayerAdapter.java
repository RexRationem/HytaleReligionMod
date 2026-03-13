package religion.api;

import java.util.UUID;

public interface PlayerAdapter {
    UUID getUniqueId();
    String getName();
    boolean isOnline();
    long getTotalPlaytimeMinutes();
    BlockPos getPosition();
    WorldAdapter getWorld();

    void sendMessage(String message);
    void giveItem(String itemId, int amount);
    void applyBuff(String buffId, int durationSeconds, int amplifier);
    void applyCurseEffect(String effectId, int durationSeconds, int amplifier);
}