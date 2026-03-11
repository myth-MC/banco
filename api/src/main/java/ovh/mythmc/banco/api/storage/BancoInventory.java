package ovh.mythmc.banco.api.storage;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Abstract base class for storage systems that use Bukkit Inventories.
 * <p>
 * This class provides a foundation for storage implementations that use Bukkit's
 * Inventory API. It handles the conversion between the abstract container interface
 * and the concrete Inventory implementation.
 * </p>
 *
 * @since 1.0.0
 */
public abstract class BancoInventory extends BancoContainer {

    /**
     * Gets the Inventory instance for the specified account.
     *
     * @param uuid UUID of the account
     * @return the Inventory instance
     * @throws IllegalArgumentException if uuid is null
     */
    @NotNull
    public abstract Inventory inventory(@NotNull UUID uuid);

    @Override
    @NotNull
    protected Collection<ItemStack> get(@NotNull UUID uuid) {
        if (uuid == null) {
            throw new IllegalArgumentException("UUID cannot be null");
        }

        final Inventory inv = inventory(uuid);
        if (inv == null) {
            return List.of();
        }

        return Arrays.stream(inv.getStorageContents())
            .toList();
    }

    @Override
    @Nullable
    protected ItemStack addItem(@NotNull UUID uuid, @NotNull ItemStack itemStack) {
        if (uuid == null) {
            throw new IllegalArgumentException("UUID cannot be null");
        }
        if (itemStack == null) {
            throw new IllegalArgumentException("ItemStack cannot be null");
        }

        final Inventory inv = inventory(uuid);
        if (inv == null) {
            return itemStack;
        }

        final Map<Integer, ItemStack> notStored = inv.addItem(itemStack);

        return notStored.values().stream()
            .findFirst()
            .orElse(null);
    }

    @Override
    @Nullable
    protected ItemStack removeItem(@NotNull UUID uuid, @NotNull ItemStack itemStack) {
        if (uuid == null) {
            throw new IllegalArgumentException("UUID cannot be null");
        }
        if (itemStack == null) {
            throw new IllegalArgumentException("ItemStack cannot be null");
        }

        final Inventory inv = inventory(uuid);
        if (inv == null) {
            return null;
        }

        return inv.removeItem(itemStack).values().stream()
            .findFirst()
            .orElse(null);
    }
}
