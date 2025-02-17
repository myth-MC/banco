package ovh.mythmc.banco.common.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.RequiredArgsConstructor;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.accounts.Account;
import ovh.mythmc.banco.common.util.PlayerUtil;

import java.math.BigDecimal;
import java.util.UUID;

@RequiredArgsConstructor
public class PlayerListener implements Listener {

    private final JavaPlugin plugin;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                //UUID uuid = PlayerUtil.getUuid(event.getPlayer().getName());
                UUID uuid = event.getPlayer().getUniqueId();
                Account account = Banco.get().getAccountManager().getByUuid(uuid);
        
                if (account == null) {
                    account = new Account(uuid, event.getPlayer().getName(), BigDecimal.valueOf(0), BigDecimal.valueOf(0));
                    Banco.get().getAccountManager().create(account);
                } else {
                    Banco.get().getAccountManager().updateName(account, event.getPlayer().getName());
                }
        
                Banco.get().getAccountManager().updateTransactions(account);
            }
        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                //UUID uuid = PlayerUtil.getUuid(event.getPlayer().getName());
                UUID uuid = event.getPlayer().getUniqueId();

                if (Banco.get().getAccountManager().getByUuid(uuid) == null)
                    return;
        
                Banco.get().getAccountManager().amount(uuid); // updates account's balance amount
            }
        });
    }

}
