package ovh.mythmc.banco.api.accounts.service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.jetbrains.annotations.NotNull;

import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.scheduler.BancoScheduler;

/**
 * Abstract base class for implementing UUID resolution with offline player caching.
 * <p>
 * This class provides a foundation for UUID resolvers that cache offline player
 * references. It handles the initial caching of server offline players and provides
 * methods for managing the cache.
 * </p>
 *
 * @since 1.0.0
 */
public abstract class AbstractLocalUUIDResolver implements LocalUUIDResolver {

    private final Map<UUID, OfflinePlayerReference> offlinePlayers = new ConcurrentHashMap<>();
    private final Map<String, UUID> uuidMap = new ConcurrentHashMap<>();

    /**
     * Gets the offline players from the server.
     * <p>
     * This method should return all offline players that the server knows about.
     * The returned iterable will be used to populate the initial cache.
     * </p>
     *
     * @return an iterable of offline player references
     */
    protected abstract Iterable<OfflinePlayerReference> serverOfflinePlayers();

    /**
     * Creates a new AbstractLocalUUIDResolver and schedules the initial cache population.
     *
     * @param scheduler the scheduler to use for async operations
     * @throws IllegalArgumentException if scheduler is null
     */
    protected AbstractLocalUUIDResolver(final @NotNull BancoScheduler scheduler) {
        if (scheduler == null) {
            throw new IllegalArgumentException("Scheduler cannot be null");
        }

        // We schedule the task twice since synchronous tasks will wait for the server to initialize
        // Once the server is initialized, we can proceed to cache the offline player references
        scheduler.run(() -> {
            scheduler.runAsync(() -> {
                try {
                    serverOfflinePlayers().forEach(offlinePlayer -> {
                        offlinePlayers.put(offlinePlayer.uuid(), offlinePlayer);
                        uuidMap.put(offlinePlayer.name(), offlinePlayer.uuid());
                    });
                    Banco.get().getLogger().info("Cached {} offline players!", offlinePlayers.size());
                } catch (Exception e) {
                    Banco.get().getLogger().error("Error caching offline players: {}", e);
                }
            });
        });
    }

    @Override
    @NotNull
    public Optional<UUID> resolve(@NotNull String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }

        return Optional.ofNullable(uuidMap.get(username));
    }

    @Override
    @NotNull
    public Optional<OfflinePlayerReference> resolveOfflinePlayer(@NotNull UUID uuid) {
        if (uuid == null) {
            throw new IllegalArgumentException("UUID cannot be null");
        }

        return Optional.ofNullable(offlinePlayers.get(uuid));
    }

    /**
     * Adds an offline player reference to the cache.
     *
     * @param offlinePlayer the offline player reference to add
     * @return true if the reference was added, false if it already existed
     * @throws IllegalArgumentException if offlinePlayer is null
     */
    protected boolean add(final @NotNull OfflinePlayerReference offlinePlayer) {
        if (offlinePlayer == null) {
            throw new IllegalArgumentException("Offline player reference cannot be null");
        }

        if (offlinePlayers.containsKey(offlinePlayer.uuid()) || uuidMap.containsKey(offlinePlayer.name())) {
            return false;
        }

        offlinePlayers.put(offlinePlayer.uuid(), offlinePlayer);
        uuidMap.put(offlinePlayer.name(), offlinePlayer.uuid());

        return true;
    }

    /**
     * Removes an offline player reference from the cache by UUID.
     *
     * @param uuid the UUID of the player to remove
     * @return true if a reference was removed, false otherwise
     * @throws IllegalArgumentException if uuid is null
     */
    protected boolean remove(final @NotNull UUID uuid) {
        if (uuid == null) {
            throw new IllegalArgumentException("UUID cannot be null");
        }
        
        final var removedOfflinePlayer = offlinePlayers.remove(uuid);
        if (removedOfflinePlayer != null) {
            uuidMap.remove(removedOfflinePlayer.name());
            return true;
        }

        return false;
    }

    /**
     * Removes an offline player reference from the cache.
     *
     * @param offlinePlayer the offline player reference to remove
     * @return true if the reference was removed, false otherwise
     * @throws IllegalArgumentException if offlinePlayer is null
     */
    protected boolean remove(final @NotNull OfflinePlayerReference offlinePlayer) {
        if (offlinePlayer == null) {
            throw new IllegalArgumentException("Offline player reference cannot be null");
        }
        return remove(offlinePlayer.uuid());
    }

    /**
     * Checks if a UUID exists in the cache.
     *
     * @param uuid the UUID to check
     * @return true if the UUID exists in the cache, false otherwise
     * @throws IllegalArgumentException if uuid is null
     */
    protected boolean has(final @NotNull UUID uuid) {
        if (uuid == null) {
            throw new IllegalArgumentException("UUID cannot be null");
        }

        return this.offlinePlayers.containsKey(uuid);
    }

    /**
     * Checks if an offline player reference exists in the cache.
     *
     * @param offlinePlayer the offline player reference to check
     * @return true if the reference exists in the cache, false otherwise
     * @throws IllegalArgumentException if offlinePlayer is null
     */
    protected boolean has(final @NotNull OfflinePlayerReference offlinePlayer) {
        if (offlinePlayer == null) {
            throw new IllegalArgumentException("Offline player reference cannot be null");
        }
        return has(offlinePlayer.uuid());
    }

    /**
     * Updates an offline player reference in the cache.
     * <p>
     * If the reference already exists, it will be removed and re-added with the new data.
     * </p>
     *
     * @param offlinePlayer the offline player reference to update
     * @return true if the reference was added/updated, false otherwise
     * @throws IllegalArgumentException if offlinePlayer is null
     */
    protected void update(final @NotNull OfflinePlayerReference offlinePlayer) {
        if (offlinePlayer == null) {
            throw new IllegalArgumentException("Offline player reference cannot be null");
        }

        OfflinePlayerReference old = offlinePlayers.put(offlinePlayer.uuid(), offlinePlayer);

        if (old != null && !old.name().equals(offlinePlayer.name())) {
            uuidMap.remove(old.name());
        }
    
        uuidMap.put(offlinePlayer.name(), offlinePlayer.uuid());
    }
}
