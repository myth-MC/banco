package ovh.mythmc.banco.common.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.events.impl.BancoItemRegisterEvent;
import ovh.mythmc.banco.api.events.impl.BancoTransactionEvent;

// Used for debugging
public final class BancoListener implements Listener {

    @EventHandler
    public void onTransaction(BancoTransactionEvent event) {
        if (!Banco.get().getSettings().get().isDebug())
            return;

        Banco.get().getLogger().info("Transaction ({}|{}): {}",
            event.account().getUuid(),
            Bukkit.getOfflinePlayer(event.account().getUuid()).getName(),
            event.amount()
        );
    }

    @EventHandler
    public void onItemRegister(BancoItemRegisterEvent event) {
        Banco.get().getLogger().info("Registered material {} with options {}: {}",
            event.bancoItem().material(),
            event.bancoItem().options(),
            event.bancoItem().value()
        );
    }
}
