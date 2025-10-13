package ovh.mythmc.banco.api.util;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.NotNull;

import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.items.BancoItem;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public final class ItemUtil {

    /**
     * Gets a list of ItemStack valued at a specified amount
     * @param amount amount of money
     * @return A list of ItemStack valued at a specified amount
     */
    public static List<ItemStack> convertAmountToItems(BigDecimal amount) {
        List<ItemStack> items = new ArrayList<>();

        for (BancoItem bancoItem : Banco.get().getItemRegistry().get().reversed()) {
            do {
                int itemAmount = Math.min((amount.divide(bancoItem.value(), RoundingMode.FLOOR)).intValue(), bancoItem.asItemStack().getMaxStackSize());

                if (itemAmount > 0) {
                    items.add(bancoItem.asItemStack(itemAmount));

                    amount = amount.subtract(bancoItem.value(itemAmount));
                }
            } while (bancoItem.value().compareTo(amount) < 0);
        }

        return items;
    }

    /**
     * Encodes an ItemStack into a Base64 string
     * @param itemStack ItemStack to erncode
     * @return A Base64-encoded string containing the ItemStack
     */
    public static String toBase64(ItemStack itemStack) throws IOException {
        final var outputStream = new ByteArrayOutputStream();
        final var dataOutput = new BukkitObjectOutputStream(outputStream);
        dataOutput.writeObject(itemStack);
        return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }

    /**
     * Decodes a Base64-encoded string into an ItemStack
     * @param base64String Base64-encoded string containing the ItemStack
     * @return An ItemStack from the supplied string
     */
    public static ItemStack fromBase64(@NotNull String base64String) throws IOException, ClassNotFoundException {
        final var inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(base64String));
        final var dataInput = new BukkitObjectInputStream(inputStream);
        return (ItemStack) dataInput.readObject(); 
    }

}
