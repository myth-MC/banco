package ovh.mythmc.banco.api.items.impl;

import java.math.BigDecimal;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import de.exlll.configlib.Configuration;
import de.exlll.configlib.Ignore;
import net.kyori.adventure.text.Component;
import ovh.mythmc.banco.api.items.BancoItem;

@Configuration
public final class CommandBancoItem implements BancoItem {

    private String command;

    private BigDecimal value;

    @Ignore
    private ItemStack itemStack;

    CommandBancoItem() {
    }

    public CommandBancoItem(@NotNull String command, @NotNull BigDecimal value) {
        this.command = command;
        this.value = value;
    }

    @Override
    public Component displayName() {
        return itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName()
            ? Component.text(itemStack.getItemMeta().getDisplayName())
            : Component.translatable(itemStack.getType().getTranslationKey());
    }

    @Override
    public BigDecimal value() {
        return this.value;
    }

    @Override
    public ItemStack asItemStack(int amount) {
        if (this.itemStack == null) {
            this.itemStack = Bukkit.getServer().getItemFactory().createItemStack(command);
        }

        final ItemStack clonedItemStack = this.itemStack.clone();
        clonedItemStack.setAmount(amount);
        return clonedItemStack;
    }
    
}
