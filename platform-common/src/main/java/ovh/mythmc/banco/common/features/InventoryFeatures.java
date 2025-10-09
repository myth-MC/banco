package ovh.mythmc.banco.common.features;

import java.util.List;

import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.storage.BancoStorage;
import ovh.mythmc.banco.common.storage.BundleContainerImpl;
import ovh.mythmc.banco.common.storage.EnderChestInventoryImpl;
import ovh.mythmc.banco.common.storage.PlayerInventoryImpl;
import ovh.mythmc.banco.common.storage.RemainderStorageImpl;
import ovh.mythmc.banco.common.storage.ShulkerBoxContainerImpl;
import ovh.mythmc.gestalt.annotations.Feature;
import ovh.mythmc.gestalt.annotations.status.FeatureDisable;
import ovh.mythmc.gestalt.annotations.status.FeatureEnable;
import ovh.mythmc.gestalt.annotations.status.FeatureInitialize;

@Feature(group = "banco", identifier = "INVENTORIES")
public final class InventoryFeatures {

    private final List<BancoStorage> storages = List.of(
        new BundleContainerImpl(),
        new PlayerInventoryImpl(),
        new EnderChestInventoryImpl(),
        new ShulkerBoxContainerImpl()
    );

    @FeatureInitialize
    public void initialize() {
        Banco.get().getStorageRegistry().setRemainderStorage(new RemainderStorageImpl());
    }

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
