package ovh.mythmc.banco.common.features;

import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.common.storage.BundleStorageImpl;
import ovh.mythmc.banco.common.storage.EnderChestInventoryImpl;
import ovh.mythmc.banco.common.storage.PlayerInventoryImpl;
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
        Banco.get().getSettings().get().getCurrency().getInventoryOrder().forEach(inventory -> {
            switch (inventory) {
                case BUNDLE -> Banco.get().getStorageManager().registerStorage(new BundleStorageImpl());
                case PLAYER_INVENTORY -> Banco.get().getStorageManager().registerStorage(new PlayerInventoryImpl());
                case ENDER_CHEST -> Banco.get().getStorageManager().registerStorage(new EnderChestInventoryImpl());
            }
        });

        enabled = true;
    }
    
}
