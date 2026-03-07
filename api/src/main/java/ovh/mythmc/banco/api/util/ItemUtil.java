package ovh.mythmc.banco.api.util;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.NotNull;

import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.items.BancoItem;
import ovh.mythmc.banco.api.items.impl.VanillaBancoItem;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

/**
 * Utility class for item-related operations.
 * <p>
 * This class provides methods for converting amounts to items and
 * encoding/decoding ItemStacks to/from Base64 strings.
 * </p>
 *
 * @since 1.0.0
 */
public final class ItemUtil {

    private ItemUtil() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Converts an amount of money into a list of ItemStacks.
     * <p>
     * This method uses the registered currency items in reverse order (highest value first)
     * to create the minimum number of items needed to represent the amount.
     * </p>
     *
     * @param amount the amount of money to convert
     * @return a list of ItemStacks representing the amount, or an empty list if amount is zero or negative
     * @throws IllegalArgumentException if amount is null
     */
    @NotNull
    public static List<ItemStack> convertAmountToItems(@NotNull BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return Collections.emptyList();
        }

        final List<ItemStack> items = new ArrayList<>();
        BigDecimal remaining = amount;

        // Process items in reverse order (highest value first)
        final List<BancoItem> currencyItems = Banco.get().getItemRegistry().get().reversed();
        
        for (final BancoItem bancoItem : currencyItems) {
            final BigDecimal itemValue = bancoItem.value();
            
            if (itemValue.compareTo(BigDecimal.ZERO) <= 0) {
                continue; // Skip items with zero or negative value
            }

            while (remaining.compareTo(itemValue) >= 0) {
                final int itemAmount = Math.min(
                    remaining.divide(itemValue, RoundingMode.FLOOR).intValue(),
                    bancoItem.asItemStack().getMaxStackSize()
                );

                if (itemAmount > 0) {
                    items.add(bancoItem.asItemStack(itemAmount));
                    remaining = remaining.subtract(bancoItem.value(itemAmount));
                } else {
                    break; // Can't create any more items of this type
                }
            }
        }

        return items;
    }

    /**
     * Calculates the maximum number of units of a given BancoItem that can be obtained
     * from a specified total value. This is useful when the player has a total amount
     * of money derived from different items and you want to know how many units of
     * a specific item that total would represent.
     *
     * @param item   the item for which the maximum stack size is calculated.
     * @param amount the total monetary value available.
     * @return The maximum number of units of the specified item that can be obtained.
     */
    public static int getMaxUnitsFromValue(@NotNull BancoItem item, @NotNull BigDecimal amount) {
        return amount.divide(item.value(), RoundingMode.FLOOR).intValue();
    }

    /**
     * Encodes an ItemStack into a Base64 string.
     * <p>
     * This method can be used to serialize ItemStacks for storage or transmission.
     * </p>
     *
     * @param itemStack the ItemStack to encode
     * @return a Base64-encoded string containing the ItemStack data
     * @throws IllegalArgumentException if itemStack is null
     * @throws IOException if an I/O error occurs during encoding
     */
    @NotNull
    public static String toBase64(@NotNull ItemStack itemStack) throws IOException {
        if (itemStack == null) {
            throw new IllegalArgumentException("ItemStack cannot be null");
        }

        try (final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             final BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)) {
            
            dataOutput.writeObject(itemStack);
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        }
    }

    /**
     * Decodes a Base64-encoded string into an ItemStack.
     * <p>
     * This method can be used to deserialize ItemStacks that were previously encoded
     * using {@link #toBase64(ItemStack)}.
     * </p>
     *
     * @param base64String the Base64-encoded string containing the ItemStack data
     * @return the decoded ItemStack
     * @throws IllegalArgumentException if base64String is null or empty
     * @throws IOException if an I/O error occurs during decoding
     * @throws ClassNotFoundException if the ItemStack class cannot be found
     */
    @NotNull
    public static ItemStack fromBase64(@NotNull String base64String) throws IOException, ClassNotFoundException {
        if (base64String == null || base64String.trim().isEmpty()) {
            throw new IllegalArgumentException("Base64 string cannot be null or empty");
        }

        try (final ByteArrayInputStream inputStream = new ByteArrayInputStream(
                Base64.getDecoder().decode(base64String));
             final BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)) {
            
            return (ItemStack) dataInput.readObject();
        }
    }

    /**
     * Determines whether a {@link BancoItem} is able to interact with other blocks
     * or entities.
     * @param bancoItem the {@link BancoItem} to check
     * @return          {@code true} if the item is able to interact, or {@code false}
     *                  otherwise
     */
    public static boolean isInteractable(@NotNull BancoItem bancoItem) {
        if (bancoItem instanceof VanillaBancoItem item) {
            if (item.customization() == null)
                return true;

            if (item.customization().restrictInteractions() == null)
                return true;

            return !item.customization().restrictInteractions();
        }

        return true;
    }

}
