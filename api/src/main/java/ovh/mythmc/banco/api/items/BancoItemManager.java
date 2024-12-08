package ovh.mythmc.banco.api.items;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import ovh.mythmc.banco.api.events.impl.BancoItemRegisterEvent;
import ovh.mythmc.banco.api.events.impl.BancoItemUnregisterEvent;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BancoItemManager {

    public static final BancoItemManager instance = new BancoItemManager();

    private final List<BancoItem> itemList = new ArrayList<>();

    public final NamespacedKey CUSTOM_ITEM_IDENTIFIER_KEY = new NamespacedKey("banco", "identifier");


    /**
     * Registers a BancoItem
     * @param items item to register
     */
    public void registerItems(@NotNull BancoItem... items) {
        Arrays.asList(items).forEach(bancoItem -> {
            // Call BancoItemRegisterEvent
            BancoItemRegisterEvent event = new BancoItemRegisterEvent(bancoItem);
            Bukkit.getPluginManager().callEvent(event);

            itemList.add(event.bancoItem());
        });
    }

    /**
     * Unregisters a BancoItem
     * @param items item to unregister
     */
    public void unregisterItems(final @NotNull BancoItem... items) {
        Arrays.asList(items).forEach(bancoItem -> {
            itemList.remove(bancoItem);

            // Call BancoItemUnregisterEvent
            BancoItemUnregisterEvent event = new BancoItemUnregisterEvent(bancoItem);
            itemList.remove(event.bancoItem());
        });
    }

    @ApiStatus.Internal
    public void clear() { itemList.clear(); }

    /**
     * Returns a list of registered items
     * @return A list with every registered items
     */
    public List<BancoItem> get() { return List.copyOf(itemList); }

    /**
     * Gets a specific BancoItem
     * @param materialName material name of an item
     * @param displayName display name of an item
     * @param glowEffect whether an item has glow effect or not
     * @param customModelData custom model data of an item
     * @return A BancoItem matching parameters or null
     */
    public BancoItem get(final @NotNull ItemStack itemStack) {
        for (BancoItem item : get()) {
            if (item.match(itemStack))
                return item;
        }

        return null;
    }

    /**
     * Checks whether an ItemStack is a valid BancoItem or not
     * @param item an ItemStack to get parameters from
     * @return true if a BancoItem matching item's parameters exists
     */
    public boolean isValid(ItemStack item) {
        return get(item) != null;
    }

    /**
     * Gets the value of an item
     * @param item a BancoItem
     * @param amount amount of items
     * @return Value of BancoItem multiplied by the amount
     */
    public BigDecimal value(final @NotNull BancoItem item, int amount) {
        return item.value().multiply(BigDecimal.valueOf(amount));
    }

}
