package ovh.mythmc.banco.api.storage;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * An inventory which provides an instance of org.bukkit.inventory.Inventory
 * @param <T> must be Inventory for now
 */
public interface BancoInventory<T> extends BancoStorage {

    /**
     *
     * @param uuid UUID of the account to get this BancoInventory from
     * @return An instance of org.bukkit.inventory.Inventory
     */
    @NotNull T get(UUID uuid);

}

