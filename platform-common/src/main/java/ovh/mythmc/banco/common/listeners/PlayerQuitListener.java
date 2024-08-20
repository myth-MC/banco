package ovh.mythmc.banco.common.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.economy.BancoHelper;
import ovh.mythmc.banco.common.util.PlayerUtil;

import java.util.UUID;

public class PlayerQuitListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID uuid = PlayerUtil.getUuid(event.getPlayer().getName());

        if (Banco.get().getAccountManager().get(uuid) == null)
            return;

        BancoHelper.get().getValue(uuid);
    }

}
