package ovh.mythmc.banco.api.items;

import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.callback.item.BancoItemRegister;
import ovh.mythmc.banco.api.callback.item.BancoItemUnregister;
import ovh.mythmc.banco.api.callback.item.BancoItemRegisterCallback;
import ovh.mythmc.banco.api.callback.item.BancoItemUnregisterCallback;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Registry for managing currency items.
 * <p>
 * This registry maintains a list of all registered currency items that can be used
 * in the Banco system. Items are registered in order, and this order determines
 * their priority when converting amounts to items.
 * </p>
 *
 * @since 1.0.0
 */
public final class BancoItemRegistry {

    /**
     * The singleton instance of the item registry.
     */
    public static final BancoItemRegistry instance = new BancoItemRegistry();

    /**
     * The namespaced key used to identify custom Banco items.
     */
    public static final NamespacedKey CUSTOM_ITEM_IDENTIFIER_KEY = new NamespacedKey("banco", "identifier");

    private final List<BancoItem> itemList = new ArrayList<>();

    private BancoItemRegistry() {
    }

    /**
     * Registers one or more currency items.
     * <p>
     * This method invokes the item registration callback before adding items to the registry,
     * allowing plugins to modify or cancel the registration.
     * </p>
     *
     * @param items the items to register
     * @throws IllegalArgumentException if items is null or contains null elements
     */
    public void register(@NotNull BancoItem... items) {
        if (items == null) {
            throw new IllegalArgumentException("Items array cannot be null");
        }

        Arrays.asList(items).forEach(bancoItem -> {
            if (bancoItem == null) {
                throw new IllegalArgumentException("Item cannot be null");
            }

            final var callback = new BancoItemRegister(bancoItem);
            BancoItemRegisterCallback.INSTANCE.invoke(callback, result -> itemList.add(result.bancoItem()));
        });
    }

    /**
     * Unregisters one or more currency items.
     * <p>
     * This method invokes the item unregistration callback before removing items from the registry,
     * allowing plugins to perform cleanup operations.
     * </p>
     *
     * @param items the items to unregister
     * @throws IllegalArgumentException if items is null or contains null elements
     */
    public void unregister(@NotNull BancoItem... items) {
        if (items == null) {
            throw new IllegalArgumentException("Items array cannot be null");
        }

        Arrays.asList(items).forEach(bancoItem -> {
            if (bancoItem == null) {
                throw new IllegalArgumentException("Item cannot be null");
            }

            final var callback = new BancoItemUnregister(bancoItem);
            BancoItemUnregisterCallback.INSTANCE.invoke(callback, result -> itemList.remove(result.bancoItem()));
        });
    }

    /**
     * Clears all registered items from the registry.
     * <p>
     * This method is marked as internal and should only be called by the plugin itself.
     * </p>
     */
    @ApiStatus.Internal
    public void clear() {
        itemList.clear();
    }

    /**
     * Gets all registered currency items.
     * <p>
     * The items are returned in registration order, which determines their priority
     * when converting amounts to items.
     * </p>
     *
     * @return an unmodifiable list of all registered items
     */
    @NotNull
    public List<BancoItem> get() {
        return Collections.unmodifiableList(itemList);
    }

    /**
     * Gets a currency item that matches the given ItemStack.
     *
     * @param itemStack the ItemStack to match
     * @return the matching BancoItem, or null if no match is found
     * @throws IllegalArgumentException if itemStack is null
     */
    @Nullable
    public BancoItem getByItemStack(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR) {
            return null;
        }

        for (final BancoItem item : get()) {
            if (item.match(itemStack)) {
                return item;
            }
        }

        return null;
    }

    /**
     * Checks whether an ItemStack is a valid currency item.
     *
     * @param item the ItemStack to check
     * @return true if the ItemStack matches a registered currency item, false otherwise
     */
    public boolean isValid(@Nullable ItemStack item) {
        return item != null && getByItemStack(item) != null;
    }

    /**
     * Checks whether the registry is using legacy currency configuration.
     * <p>
     * Legacy mode indicates that currency items are configured in the old format.
     * </p>
     *
     * @return true if using legacy configuration, false otherwise
     */
    public boolean isLegacy() {
        return Banco.get().getSettings().get().getCurrency().getItems() != null;
    }
}
