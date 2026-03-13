package religion.api;

import religion.service.ReligionService;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface ServerBridge {

    void logInfo(String message);

    void broadcast(String message);

    Optional<PlayerAdapter> getPlayer(UUID playerId);

    Optional<PlayerAdapter> getOnlinePlayerByName(String name);

    Collection<PlayerAdapter> getOnlinePlayers();

    long getCurrentEpochMillis();

    void registerHooks(ReligionService religionService);
}