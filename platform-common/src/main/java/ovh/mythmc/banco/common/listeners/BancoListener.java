package ovh.mythmc.banco.common.listeners;

import org.bukkit.Bukkit;

import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.callbacks.BancoTransactionCallback;
import ovh.mythmc.banco.api.callbacks.item.BancoItemRegisterCallback;
import ovh.mythmc.gestalt.key.IdentifierKey;

// Used for debugging
public final class BancoListener {

    private final static IdentifierKey DEBUG_KEY = IdentifierKey.of("banco", "debug");

    public void registerCallbacks() {
        BancoTransactionCallback.INSTANCE.registerListener(DEBUG_KEY, (transaction) -> {
            if (!Banco.get().getSettings().get().isDebug())
                return;

            Banco.get().getLogger().info("Transaction ({}|{}): {} - operation: {}",
                transaction.account().getUuid(),
                Bukkit.getOfflinePlayer(transaction.account().getUuid()).getName(),
                transaction.amount(),
                transaction.operation()
            );
        });

        BancoItemRegisterCallback.INSTANCE.registerListener(DEBUG_KEY, (item) -> {
            Banco.get().getLogger().info("Registered ItemStack {} with value {}",
                item.asItemStack(),
                item.value()
            );
        });
    }

    public void unregisterCallbacks() {
        BancoTransactionCallback.INSTANCE.unregisterListeners(DEBUG_KEY);
        BancoItemRegisterCallback.INSTANCE.unregisterListeners(DEBUG_KEY);
    }

}
