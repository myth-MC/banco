package ovh.mythmc.banco.common.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.economy.BancoHelper;
import ovh.mythmc.banco.api.accounts.AccountManager;
import ovh.mythmc.banco.common.util.PlayerUtil;

import java.util.UUID;

public class PlayerQuitListener implements Listener {

    AccountManager accountManager = Banco.get().getAccountManager();

    @EventHandler
    public void onPlayerJoin(PlayerQuitEvent event) {
        UUID uuid = PlayerUtil.getUuid(event.getPlayer().getName());

        if (accountManager.get(uuid) == null)
            return;

        BancoHelper.get().getInventoryValue(uuid);
    }

}
