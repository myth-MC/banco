package ovh.mythmc.banco.api.storage;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.callback.storage.BancoStorageRegister;
import ovh.mythmc.banco.api.callback.storage.BancoStorageUnregister;
import ovh.mythmc.banco.api.callback.storage.BancoStorageRegisterCallback;
import ovh.mythmc.banco.api.callback.storage.BancoStorageUnregisterCallback;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Vector;
import java.util.stream.Collectors;

/**
 * Registry for managing currency storage systems.
 * <p>
 * This registry maintains a list of all registered storage systems that can be used
 * to store currency items. Storage systems are registered in order, and this order
 * determines their priority when storing or retrieving currency.
 * </p>
 *
 * @since 1.0.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BancoStorageRegistry {

    /**
     * The singleton instance of the storage registry.
     */
    public static final BancoStorageRegistry instance = new BancoStorageRegistry();

    private static final Collection<BancoStorage> storages = new Vector<>(0);

    private BancoStorage remainderStorage = null;

    /**
     * Gets all registered storage systems.
     *
     * @return an unmodifiable collection of registered storage systems
     */
    @NotNull
    public Collection<BancoStorage> get() {
        return Collections.unmodifiableCollection(storages);
    }

    /**
     * Gets registered storage systems in the order specified by configuration.
     * <p>
     * The order is determined by the inventory order setting in the plugin configuration.
     * If a remainder storage is set, it will be added at the end of the list.
     * </p>
     *
     * @return a collection of registered and enabled storage systems in order
     */
    @NotNull
    public Collection<BancoStorage> getByOrder() {
        final Collection<BancoStorage> orderedStorage = Banco.get().getSettings().get().getCurrency().getInventoryOrder().stream()
            .map(this::getByFriendlyName)
            .filter(storage -> storage != null)
            .collect(Collectors.toList());

        if (remainderStorage != null) {
            orderedStorage.add(remainderStorage);
        }

        return orderedStorage;
    }

    /**
     * Registers one or more storage systems.
     * <p>
     * This method invokes the storage registration callback before adding storage
     * systems to the registry, allowing plugins to modify or cancel the registration.
     * </p>
     *
     * @param bancoStorages the storage systems to register
     * @throws IllegalArgumentException if bancoStorages is null or contains null elements
     */
    public void registerStorage(final @NotNull BancoStorage... bancoStorages) {
        if (bancoStorages == null) {
            throw new IllegalArgumentException("Storage systems array cannot be null");
        }

        Arrays.stream(bancoStorages)
            .forEach(storage -> {
                if (storage == null) {
                    throw new IllegalArgumentException("Storage system cannot be null");
                }

                final var callback = new BancoStorageRegister(storage);
                BancoStorageRegisterCallback.INSTANCE.invoke(callback, result -> storages.add(result.bancoStorage()));
            });
    }

    /**
     * Unregisters one or more storage systems.
     * <p>
     * This method invokes the storage unregistration callback before removing storage
     * systems from the registry, allowing plugins to perform cleanup operations.
     * </p>
     *
     * @param bancoStorages the storage systems to unregister
     * @throws IllegalArgumentException if bancoStorages is null or contains null elements
     */
    public void unregisterStorage(final @NotNull BancoStorage... bancoStorages) {
        if (bancoStorages == null) {
            throw new IllegalArgumentException("Storage systems array cannot be null");
        }

        Arrays.stream(bancoStorages)
            .forEach(storage -> {
                if (storage == null) {
                    throw new IllegalArgumentException("Storage system cannot be null");
                }

                final var callback = new BancoStorageUnregister(storage);
                BancoStorageUnregisterCallback.INSTANCE.invoke(callback, result -> storages.remove(result.bancoStorage()));
            });
    }

    /**
     * Sets the remainder storage system.
     * <p>
     * The remainder storage is used to store any currency that cannot be stored
     * in the primary storage systems. It is added at the end of the ordered list.
     * </p>
     *
     * @param remainderStorage the remainder storage system
     * @throws IllegalArgumentException if remainderStorage is null
     */
    @Internal
    public void setRemainderStorage(final @NotNull BancoStorage remainderStorage) {
        if (remainderStorage == null) {
            throw new IllegalArgumentException("Remainder storage cannot be null");
        }
        this.remainderStorage = remainderStorage;
    }

    /**
     * Gets a registered storage system by its friendly name.
     *
     * @param friendlyName the friendly name of the storage system
     * @return the storage system if found, null otherwise
     * @throws IllegalArgumentException if friendlyName is null or empty
     */
    @Nullable
    public BancoStorage getByFriendlyName(@NotNull String friendlyName) {
        if (friendlyName == null || friendlyName.trim().isEmpty()) {
            throw new IllegalArgumentException("Friendly name cannot be null or empty");
        }

        return storages.stream()
            .filter(storage -> storage.friendlyName().equals(friendlyName))
            .findFirst()
            .orElse(null);
    }
}
