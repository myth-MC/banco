package ovh.mythmc.banco.common.listeners;

import org.bukkit.Bukkit;

import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.callback.transaction.BancoTransactionProcessCallback;
import ovh.mythmc.banco.api.callback.item.BancoItemRegisterCallback;

// Used for debugging
public final class BancoListener {

    private final static String DEBUG_KEY = "banco:debug";

    public void registerCallbacks() {
        BancoTransactionProcessCallback.INSTANCE.registerListener(DEBUG_KEY, (transaction, cancelled) -> {
            if (!Banco.get().getSettings().get().isDebug())
                return;

            Banco.get().getLogger().debug("Transaction ({}|{}): {} - operation: {} - cancelled: {}",
                transaction.account().getUuid(),
                Bukkit.getOfflinePlayer(transaction.account().getUuid()).getName(),
                transaction.amount(),
                transaction.operation(),
                cancelled
            );
        });

        BancoItemRegisterCallback.INSTANCE.registerListener(DEBUG_KEY, (item) -> {
            if (!Banco.get().getSettings().get().isDebug())
                return;
                
            Banco.get().getLogger().debug("Registered ItemStack {} with value {}",
                item.asItemStack(),
                item.value()
            );
        });
    }

    public void unregisterCallbacks() {
        BancoTransactionProcessCallback.INSTANCE.unregisterListeners(DEBUG_KEY);
        BancoItemRegisterCallback.INSTANCE.unregisterListeners(DEBUG_KEY);
    }

}
