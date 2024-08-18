package ovh.mythmc.banco.common.listeners;

import org.bukkit.Bukkit;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.event.BancoEvent;
import ovh.mythmc.banco.api.event.BancoEventListener;
import ovh.mythmc.banco.api.event.impl.BancoItemRegisterEvent;
import ovh.mythmc.banco.api.event.impl.BancoTransactionEvent;

public final class BancoListener implements BancoEventListener {
    // Debugger
    @Override
    public void handle(BancoEvent event) {
        if (!Banco.get().getSettings().get().isDebug())
            return;

        if (event instanceof BancoTransactionEvent transactionEvent) {
            Banco.get().getLogger().info("Transaction ({}|{}): {}",
                    transactionEvent.account().getUuid(),
                    Bukkit.getOfflinePlayer(transactionEvent.account().getUuid()).getName(),
                    transactionEvent.amount()
            );
        } else if (event instanceof BancoItemRegisterEvent itemRegisterEvent) {
            Banco.get().getLogger().info("Registered material {} with displayName {} and customModelData {}: {}",
                    itemRegisterEvent.bancoItem().name(),
                    itemRegisterEvent.bancoItem().displayName(),
                    itemRegisterEvent.bancoItem().customModelData(),
                    itemRegisterEvent.bancoItem().value()
            );
        }
    }
}
