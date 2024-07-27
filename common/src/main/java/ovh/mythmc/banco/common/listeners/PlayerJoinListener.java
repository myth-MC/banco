package ovh.mythmc.banco.common.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.economy.Account;
import ovh.mythmc.banco.api.economy.AccountManager;

import java.util.UUID;

public class PlayerJoinListener implements Listener {

    AccountManager accountManager = Banco.get().getAccountManager();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        Account account = accountManager.get(uuid);

        if (account == null) {
            account = new Account(uuid, 0, 0);
            accountManager.add(new Account(uuid, 0, 0));
        }

        accountManager.updateTransactions(account);
    }

}
