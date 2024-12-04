package ovh.mythmc.banco.api.items;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.banco.api.events.impl.BancoItemRegisterEvent;
import ovh.mythmc.banco.api.events.impl.BancoItemUnregisterEvent;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BancoItemManager {

    public static final BancoItemManager instance = new BancoItemManager();
    private static final List<BancoItem> itemsList = new ArrayList<>();

    /**
     * Registers a BancoItem
     * @param items item to register
     */
    public void registerItems(@NotNull BancoItem... items) {
        Arrays.asList(items).forEach(bancoItem -> {
            bancoItem = validate(bancoItem);

            // Call BancoItemRegisterEvent
            BancoItemRegisterEvent event = new BancoItemRegisterEvent(bancoItem);
            Bukkit.getPluginManager().callEvent(event);

            itemsList.add(event.bancoItem());
        });
    }

    /**
     * Unregisters a BancoItem
     * @param items item to unregister
     */
    public void unregisterItems(final @NotNull BancoItem... items) {
        Arrays.asList(items).forEach(bancoItem -> {
            itemsList.remove(bancoItem);

            // Call BancoItemUnregisterEvent
            BancoItemUnregisterEvent event = new BancoItemUnregisterEvent(bancoItem);
            itemsList.remove(event.bancoItem());
        });
    }

    @ApiStatus.Internal
    public void clear() { itemsList.clear(); }

    /**
     * Returns a list of registered items
     * @return A list with every registered items
     */
    public List<BancoItem> get() { return List.copyOf(itemsList); }

    /**
     * Gets a specific BancoItem
     * @param materialName material name of an item
     * @param displayName display name of an item
     * @param glowEffect whether an item has glow effect or not
     * @param customModelData custom model data of an item
     * @return A BancoItem matching parameters or null
     */
    public BancoItem get(final @NotNull String materialName,
                         final @NotNull String displayName,
                         final boolean glowEffect,
                         final Integer customModelData) {
        for (BancoItem item : get()) {
            if (Objects.equals(materialName, item.name())
                    && Objects.equals(displayName, item.displayName())
                    && Objects.equals(glowEffect, item.glowEffect())
                    && Objects.equals(customModelData, item.customModelData())) {

                return item;
            }
        }

        return null;
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

    private BancoItem validate(@NotNull BancoItem item) {
        if (item.glowEffect() == null)
            item = item.withGlowEffect(false);

        return item;
    }

}
