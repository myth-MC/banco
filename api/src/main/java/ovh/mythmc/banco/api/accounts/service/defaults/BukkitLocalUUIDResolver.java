package ovh.mythmc.banco.api.accounts.service.defaults;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import ovh.mythmc.banco.api.accounts.service.AbstractLocalUUIDResolver;
import ovh.mythmc.banco.api.accounts.service.OfflinePlayerReference;
import ovh.mythmc.banco.api.scheduler.BancoScheduler;

/**
 * Bukkit implementation of LocalUUIDResolver.
 * <p>
 * This implementation uses Bukkit's OfflinePlayer API to resolve UUIDs and
 * cache offline player references. It listens to player join/quit events to
 * keep the cache up to date.
 * </p>
 *
 * @since 1.0.0
 */
public final class BukkitLocalUUIDResolver extends AbstractLocalUUIDResolver implements Listener {

    /**
     * Creates a new BukkitLocalUUIDResolver.
     *
     * @param scheduler the scheduler to use for async operations
     * @throws IllegalArgumentException if scheduler is null
     */
    public BukkitLocalUUIDResolver(final @NotNull BancoScheduler scheduler) {
        super(scheduler);
    }

    @Override
    protected Iterable<OfflinePlayerReference> serverOfflinePlayers() {
        final OfflinePlayer[] offlinePlayers = Bukkit.getOfflinePlayers();
        if (offlinePlayers == null) {
            return List.of();
        }

        return Arrays.stream(offlinePlayers)
            .map(OfflinePlayerReference::from)
            .toList();
    }

    /**
     * Handles player join events to update the cache.
     *
     * @param event the player join event
     */
    @EventHandler
    public void onPlayerJoin(final @NotNull PlayerJoinEvent event) {
        if (event == null || event.getPlayer() == null) {
            return;
        }

        final OfflinePlayer offlinePlayer = event.getPlayer();
        update(OfflinePlayerReference.from(offlinePlayer));
    }

    /**
     * Handles player quit events to update the cache.
     *
     * @param event the player quit event
     */
    @EventHandler
    public void onPlayerQuit(final @NotNull PlayerQuitEvent event) {
        if (event == null || event.getPlayer() == null) {
            return;
        }

        final OfflinePlayer offlinePlayer = event.getPlayer();
        update(OfflinePlayerReference.from(offlinePlayer));
    }
}
