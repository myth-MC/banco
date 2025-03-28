package ovh.mythmc.banco.api.storage;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

/**
 * An inventory which provides an instance of org.bukkit.inventory.Inventory
 */
public abstract class BancoInventory extends BancoContainer {

    /**
     *
     * @param uuid UUID of the account to get this BancoInventory from
     * @return An instance of org.bukkit.inventory.Inventory
     */
    public @NotNull abstract Inventory inventory(UUID uuid);

    @Override
    protected Collection<ItemStack> get(UUID uuid) {
        return Arrays.stream(inventory(uuid).getStorageContents()).toList();
    }

    @Override
    protected ItemStack addItem(UUID uuid, ItemStack itemStack) {
        Map<Integer, ItemStack> notStored = inventory(uuid).addItem(itemStack);

        return notStored.values().stream()
            .findFirst()
            .orElse(null);
    }

    @Override
    protected ItemStack removeItem(UUID uuid, ItemStack itemStack) {
        return inventory(uuid).removeItem(itemStack).values().stream()
            .findFirst()
            .orElse(null);
    }

}

