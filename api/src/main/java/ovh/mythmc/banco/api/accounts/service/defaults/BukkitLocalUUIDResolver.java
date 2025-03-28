package ovh.mythmc.banco.api.accounts.service.defaults;

import java.util.Arrays;

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

public final class BukkitLocalUUIDResolver extends AbstractLocalUUIDResolver implements Listener {

    public BukkitLocalUUIDResolver(final @NotNull BancoScheduler scheduler) {
        super(scheduler);
    }

    @Override
    protected Iterable<OfflinePlayerReference> serverOfflinePlayers() {
        return Arrays.stream(Bukkit.getOfflinePlayers())
            .map(OfflinePlayerReference::from)
            .toList();
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final OfflinePlayer offlinePlayer = event.getPlayer();
        update(OfflinePlayerReference.from(offlinePlayer));
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        final OfflinePlayer offlinePlayer = event.getPlayer();
        update(OfflinePlayerReference.from(offlinePlayer));
    }
    
}
