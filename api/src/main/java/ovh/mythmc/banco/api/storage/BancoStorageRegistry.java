package ovh.mythmc.banco.api.storage;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.callbacks.storage.BancoStorageRegister;
import ovh.mythmc.banco.api.callbacks.storage.BancoStorageRegisterCallback;
import ovh.mythmc.banco.api.callbacks.storage.BancoStorageUnregister;
import ovh.mythmc.banco.api.callbacks.storage.BancoStorageUnregisterCallback;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BancoStorageRegistry {

    public static final BancoStorageRegistry instance = new BancoStorageRegistry();
    private static final Collection<BancoStorage> storages = new Vector<>(0);

    private BancoStorage remainderStorage = null;

    /**
     *
     * @return A Collection of registered BancoStorages
     */
    public Collection<BancoStorage> get() { return List.copyOf(storages); }

    /**
     *
     * @return A Collection of registered and enabled BancoStorages from settings
     */
    public Collection<BancoStorage> getByOrder() { 
        final Collection<BancoStorage> orderedStorage = Banco.get().getSettings().get().getCurrency().getInventoryOrder().stream()
            .map(this::getByFriendlyName)
            .filter(o -> o != null)
            .collect(Collectors.toList());

        if (remainderStorage != null)
            orderedStorage.add(remainderStorage);

        return orderedStorage;
    }

    /**
     * Registers this BancoStorage
     * @param bancoStorages BancoStorages to register
     */
    public void registerStorage(final @NotNull BancoStorage... bancoStorages) {
        Arrays.asList(bancoStorages).stream()
            .forEach(storage -> {
                var callback = new BancoStorageRegister(storage);
                BancoStorageRegisterCallback.INSTANCE.invoke(callback, result -> storages.add(result.bancoStorage()));
            });
    }

    /**
     * Unregisters this BancoStorage
     * @param bancoStorages BancoStorages to unregister
     */
    public void unregisterStorage(final @NotNull BancoStorage... bancoStorages) {
        Arrays.asList(bancoStorages).stream()
            .forEach(storage -> {
                var callback = new BancoStorageUnregister(storage);
                BancoStorageUnregisterCallback.INSTANCE.invoke(callback, result -> storages.remove(result.bancoStorage()));
            });
    }

    @Internal
    public void setRemainderStorage(final @NotNull BancoStorage remainderStorage) {
        this.remainderStorage = remainderStorage;
    }

    /**
     * Gets a registered BancoStorage by its friendly name
     * @param friendlyName friendly name of the BancoStorage to get
     * @return A Collection of registered BancoStorages
     */
    public BancoStorage getByFriendlyName(@NotNull String friendlyName) {
        return storages.stream().filter(storage -> storage.friendlyName().equals(friendlyName)).findFirst().orElse(null);
    }

}
