package ovh.mythmc.banco.common.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.RequiredArgsConstructor;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.common.util.PlayerUtil;

import java.util.UUID;

@RequiredArgsConstructor
public class PlayerQuitListener implements Listener {

    private final JavaPlugin plugin;

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                UUID uuid = PlayerUtil.getUuid(event.getPlayer().getName());

                if (Banco.get().getAccountManager().get(uuid) == null)
                    return;
        
                Banco.get().getAccountManager().amount(uuid); // updates account's balance amount
            }
        });
    }

}
