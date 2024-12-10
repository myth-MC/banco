package ovh.mythmc.banco.common.features;

import java.util.List;

import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.storage.BancoStorage;
import ovh.mythmc.banco.common.storage.BundleStorageImpl;
import ovh.mythmc.banco.common.storage.EnderChestInventoryImpl;
import ovh.mythmc.banco.common.storage.PlayerInventoryImpl;
import ovh.mythmc.gestalt.annotations.Feature;
import ovh.mythmc.gestalt.annotations.status.FeatureDisable;
import ovh.mythmc.gestalt.annotations.status.FeatureEnable;

@Feature(group = "banco", identifier = "INVENTORIES")
public final class InventoryFeatures {

    private final List<BancoStorage> storages = List.of(
        new BundleStorageImpl(),
        new PlayerInventoryImpl(),
        new EnderChestInventoryImpl()
    );

    @FeatureEnable
    public void enable() {
        // Register banco storages
        storages.forEach(storage -> Banco.get().getStorageRegistry().registerStorage(storage));
    }

    @FeatureDisable
    public void disable() {
        // Unregister banco storages
        storages.forEach(storage -> Banco.get().getStorageRegistry().unregisterStorage(storage));
    }
    
}
