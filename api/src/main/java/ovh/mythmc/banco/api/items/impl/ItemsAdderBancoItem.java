package ovh.mythmc.banco.api.items.impl;

import java.math.BigDecimal;

import org.bukkit.inventory.ItemStack;

import dev.lone.itemsadder.api.CustomStack;
import net.kyori.adventure.text.Component;
import ovh.mythmc.banco.api.items.BancoItem;

public record ItemsAdderBancoItem(String identifier, BigDecimal value) implements BancoItem {

    @Override
    public Component displayName() {
        return Component.text(getCustomStack().getDisplayName());
    }

    @Override
    public boolean match(ItemStack itemStack) {
        return getCustomStack().getItemStack().isSimilar(itemStack);
    }

    @Override
    public ItemStack asItemStack(int amount) {
        ItemStack itemStack = getCustomStack().getItemStack();
        itemStack.setAmount(amount);
        return itemStack;
    }

    private CustomStack getCustomStack() {
        return CustomStack.getInstance(identifier);
    }
    
}
