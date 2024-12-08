package ovh.mythmc.banco.common.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.RequiredArgsConstructor;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.accounts.Account;
import ovh.mythmc.banco.common.util.PlayerUtil;

import java.math.BigDecimal;
import java.util.UUID;

@RequiredArgsConstructor
public class PlayerJoinListener implements Listener {

    private final JavaPlugin plugin;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                UUID uuid = PlayerUtil.getUuid(event.getPlayer().getName());
                Account account = Banco.get().getAccountManager().get(uuid);
        
                if (account == null) {
                    account = new Account(uuid, BigDecimal.valueOf(0), BigDecimal.valueOf(0));
                    Banco.get().getAccountManager().create(account);
                }
        
                Banco.get().getAccountManager().updateTransactions(account);
            }
        });
    }

}
