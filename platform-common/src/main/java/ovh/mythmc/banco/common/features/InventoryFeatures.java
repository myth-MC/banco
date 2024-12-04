package ovh.mythmc.banco.common.features;

import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.configuration.sections.CurrencyConfig;
import ovh.mythmc.banco.common.inventories.EnderChestInventoryImpl;
import ovh.mythmc.banco.common.inventories.PlayerInventoryImpl;
import ovh.mythmc.gestalt.annotations.Feature;
import ovh.mythmc.gestalt.annotations.status.FeatureEnable;

@Feature(group = "banco", identifier = "INVENTORIES")
public final class InventoryFeatures {

    private boolean enabled = false;

    @FeatureEnable
    public void enable() {
        if (enabled)
            return;

        // Register banco inventories
        CurrencyConfig.InventoryPriority inventoryPriority = Banco.get().getSettings().get().getCurrency().getInventoryPriority();
        boolean countEnderChest = Banco.get().getSettings().get().getCurrency().isCountEnderChest();

        switch (inventoryPriority) {
            case PLAYER_INVENTORY -> {
                Banco.get().getStorageManager().registerStorage(new PlayerInventoryImpl());
                if (countEnderChest)
                    Banco.get().getStorageManager().registerStorage(new EnderChestInventoryImpl());
            }
            case ENDER_CHEST -> Banco.get().getStorageManager().registerStorage(new EnderChestInventoryImpl(),
                    new PlayerInventoryImpl());
        }

        enabled = true;
    }
    
}
