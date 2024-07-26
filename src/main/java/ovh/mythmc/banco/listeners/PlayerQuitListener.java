package ovh.mythmc.banco.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import ovh.mythmc.banco.Banco;
import ovh.mythmc.banco.economy.Account;
import ovh.mythmc.banco.economy.AccountManager;

import java.util.UUID;

public class PlayerQuitListener implements Listener {

    AccountManager accountManager = Banco.get().getAccountManager();

    @EventHandler
    public void onPlayerJoin(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        Account account = accountManager.getAccount(uuid);

        if (account == null)
            return;

        accountManager.getActualAmount(event.getPlayer());
    }

}
