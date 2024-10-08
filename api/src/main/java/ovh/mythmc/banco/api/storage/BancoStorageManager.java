package ovh.mythmc.banco.api.storage;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.event.impl.BancoStorageRegisterEvent;
import ovh.mythmc.banco.api.event.impl.BancoStorageUnregisterEvent;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BancoStorageManager {

    public static final BancoStorageManager instance = new BancoStorageManager();
    private static final Collection<BancoStorage> storages = new Vector<>(0);

    /**
     *
     * @return A Collection of registered BancoStorages
     */
    @ApiStatus.Internal
    public Collection<BancoStorage> get() { return storages; }

    /**
     * Registers this BancoStorage
     * @param bancoStorages BancoStorages to register
     */
    public void registerStorage(final @NotNull BancoStorage... bancoStorages) {
        List<BancoStorage> bancoStorageList = Arrays.asList(bancoStorages);
        storages.addAll(bancoStorageList);

        // Call BancoInventoryRegisterEvent
        bancoStorageList.forEach(bancoInventory -> Banco.get().getEventManager().publish(new BancoStorageRegisterEvent(bancoInventory)));
    }

    /**
     * Unregisters this BancoStorage
     * @param bancoStorages BancoStorages to unregister
     */
    public void unregisterStorage(final @NotNull BancoStorage... bancoStorages) {
        List<BancoStorage> bancoStorageList = Arrays.asList(bancoStorages);
        storages.removeAll(bancoStorageList);

        // Call BancoInventoryUnregisterEvent
        bancoStorageList.forEach(bancoInventory -> Banco.get().getEventManager().publish(new BancoStorageUnregisterEvent(bancoInventory)));
    }

}
