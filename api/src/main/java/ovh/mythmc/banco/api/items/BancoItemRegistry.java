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
public final class BancoItemRegistry {

    public static final BancoItemRegistry instance = new BancoItemRegistry();

    private final List<BancoItem> itemList = new ArrayList<>();

    public final NamespacedKey CUSTOM_ITEM_IDENTIFIER_KEY = new NamespacedKey("banco", "identifier");


    /**
     * Registers a BancoItem
     * @param items item to register
     */
    public void register(@NotNull BancoItem... items) {
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
    public void unregister(final @NotNull BancoItem... items) {
        Arrays.asList(items).forEach(bancoItem -> {
            itemList.remove(bancoItem);

            // Call BancoItemUnregisterEvent
            BancoItemUnregisterEvent event = new BancoItemUnregisterEvent(bancoItem);
            Bukkit.getPluginManager().callEvent(event);
            
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
     * @param itemStack ItemStack that should match a BancoItem
     * @return A BancoItem matching parameters or null
     */
    public BancoItem getByItemStack(final @NotNull ItemStack itemStack) {
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
        return getByItemStack(item) != null;
    }

    /**
     * Gets the value of an item
     * @param item a BancoItem
     * @param amount amount of items
     * @return Value of BancoItem multiplied by the amount
     */
    @Deprecated(since = "1.0", forRemoval = true)
    public BigDecimal value(final @NotNull BancoItem item, int amount) {
        return item.value().multiply(BigDecimal.valueOf(amount));
    }

}
