package ovh.mythmc.banco.api.accounts.service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;

import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.scheduler.BancoScheduler;

public abstract class AbstractLocalUUIDResolver implements LocalUUIDResolver {

    private final Set<OfflinePlayerReference> offlinePlayers = new HashSet<>();

    protected abstract Iterable<OfflinePlayerReference> serverOfflinePlayers();

    protected AbstractLocalUUIDResolver(final @NotNull BancoScheduler scheduler) {
        // We schedule the task twice since synchronous tasks will wait for the server to initialize
        // Once the server is initialized, we can proceed to cache the offline player references
        scheduler.run(() -> {
            scheduler.runAsync(() -> {
                serverOfflinePlayers().forEach(offlinePlayers::add);

                Banco.get().getLogger().info("Cached {} offline players!", offlinePlayers.size());
            });
        });

    }

    @Override
    public Set<OfflinePlayerReference> references() {
        return this.offlinePlayers;
    }

    @Override
    public @NotNull Optional<UUID> resolve(@NotNull String username) {
        return offlinePlayers.stream()
            .filter(offlinePlayer -> offlinePlayer.name().equals(username))
            .map(OfflinePlayerReference::uuid)
            .findAny();
    }

    @Override
    public @NotNull Optional<OfflinePlayerReference> resolveOfflinePlayer(@NotNull UUID uuid) {
        return this.offlinePlayers.stream()
            .filter(offlinePlayer -> offlinePlayer.uuid().equals(uuid))
            .findAny();
    }

    protected boolean add(final @NotNull OfflinePlayerReference offlinePlayer) {
        return this.offlinePlayers.add(offlinePlayer);
    }

    protected boolean remove(final @NotNull UUID uuid) {
        return this.offlinePlayers.removeIf(offlinePlayer -> offlinePlayer.uuid().equals(uuid));
    }

    protected boolean remove(final @NotNull OfflinePlayerReference offlinePlayer) {
        return remove(offlinePlayer.uuid());
    }

    protected boolean has(final @NotNull UUID uuid) {
        return this.offlinePlayers.stream()
            .anyMatch(offlinePlayer -> offlinePlayer.uuid().equals(uuid));
    }

    protected boolean has(final @NotNull OfflinePlayerReference offlinePlayer) {
        return has(offlinePlayer.uuid());
    }

    protected boolean update(final @NotNull OfflinePlayerReference offlinePlayer) {
        if (has(offlinePlayer))
            remove(offlinePlayer);

        return add(offlinePlayer);
    }
    
}
