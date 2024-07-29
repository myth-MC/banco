package ovh.mythmc.banco.common.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.economy.AccountManager;

import java.util.UUID;

public class PlayerQuitListener implements Listener {

    AccountManager accountManager = Banco.get().getAccountManager();

    @EventHandler
    public void onPlayerJoin(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();

        if (accountManager.get(uuid) == null)
            return;

        Banco.get().getInventoryValue(uuid);
    }

}
