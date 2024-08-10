package ovh.mythmc.banco.common.listeners;

import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.event.BancoEvent;
import ovh.mythmc.banco.api.event.BancoEventListener;
import ovh.mythmc.banco.api.event.impl.BancoTransactionEvent;

public final class BancoListener implements BancoEventListener {
    @Override
    public void handle(BancoEvent event) {
        if (event instanceof BancoTransactionEvent transactionEvent) {
            if (Banco.get().getSettings().get().isDebug())
                Banco.get().getLogger().info("Transaction (" + transactionEvent.account().getUuid() + "): " + transactionEvent.amount());
        }
    }
}
