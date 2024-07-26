package ovh.mythmc.banco.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import ovh.mythmc.banco.Banco;
import ovh.mythmc.banco.economy.Account;
import ovh.mythmc.banco.economy.AccountManager;

import java.util.UUID;

public class PlayerJoinListener implements Listener {

    AccountManager accountManager = Banco.get().getAccountManager();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        Account account = accountManager.getAccount(uuid);

        if (account == null)
            accountManager.createAccount(uuid);

        accountManager.add(event.getPlayer(), accountManager.getTransactions(event.getPlayer()));
    }

}
