package ovh.mythmc.banco.api.items.impl;

import java.io.IOException;
import java.math.BigDecimal;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import de.exlll.configlib.Configuration;
import de.exlll.configlib.Ignore;
import net.kyori.adventure.text.Component;
import ovh.mythmc.banco.api.items.BancoItem;
import ovh.mythmc.banco.api.util.ItemUtil;

@Configuration
public final class Base64BancoItem implements BancoItem {

    @Ignore
    private ItemStack itemStack;

    private String string;

    private BigDecimal value;

    public Base64BancoItem(@NotNull String string, @NotNull BigDecimal value) {
        this.itemStack = encode(string);
        this.string = string;
        this.value = value;
    }

    Base64BancoItem() {
    }

    @Override
    public Component displayName() {
        if (this.itemStack.getItemMeta() != null && this.itemStack.getItemMeta().getDisplayName() != null)
            return Component.text(this.itemStack.getItemMeta().getDisplayName());

        return Component.translatable(this.itemStack.getTranslationKey());
    }
    
    @Override
    public ItemStack asItemStack(int amount) {
        if (this.itemStack == null)
            this.itemStack = encode(string);

        final var item = itemStack.clone();
        item.setAmount(amount);
        return item;
    }

    @Override
    public BigDecimal value() {
        return this.value;
    }

    private static ItemStack encode(@NotNull String string) {
        try {
            return ItemUtil.fromBase64(string);
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace(System.err);
        }

        return null;
    }
    
}
