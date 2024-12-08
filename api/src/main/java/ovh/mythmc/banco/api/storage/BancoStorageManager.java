package ovh.mythmc.banco.api.storage;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import ovh.mythmc.banco.api.events.impl.BancoStorageRegisterEvent;
import ovh.mythmc.banco.api.events.impl.BancoStorageUnregisterEvent;

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
    public Collection<BancoStorage> get() { return List.copyOf(storages); }

    /**
     * Registers this BancoStorage
     * @param bancoStorages BancoStorages to register
     */
    public void registerStorage(final @NotNull BancoStorage... bancoStorages) {
        Arrays.asList(bancoStorages).stream()
            .forEach(storage -> {
                BancoStorageRegisterEvent event = new BancoStorageRegisterEvent(storage);
                Bukkit.getPluginManager().callEvent(event);

                storages.add(event.bancoStorage());
            });
    }

    /**
     * Unregisters this BancoStorage
     * @param bancoStorages BancoStorages to unregister
     */
    public void unregisterStorage(final @NotNull BancoStorage... bancoStorages) {
        Arrays.asList(bancoStorages).stream()
            .forEach(storage -> {
                BancoStorageUnregisterEvent event = new BancoStorageUnregisterEvent(storage);
                Bukkit.getPluginManager().callEvent(event);

                storages.remove(event.bancoStorage());
            });
    }

}
