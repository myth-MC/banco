package ovh.mythmc.banco.api.items.impl;

import java.math.BigDecimal;

import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import net.kyori.adventure.text.Component;
import ovh.mythmc.banco.api.items.BancoItem;

public record SlimefunBancoItem(String identifier, BigDecimal value) implements BancoItem {

    @Override
    public Component displayName() {
        final var slimefunItem = SlimefunItem.getById(identifier);

        return slimefunItem == null 
            ? Component.empty() 
            : Component.text(slimefunItem.getItemName());
    }

    @Override
    public boolean match(ItemStack itemStack) {
        var optionalSlimefunItem = SlimefunItem.getOptionalByItem(itemStack);

        if (optionalSlimefunItem.isEmpty())
            return false;

        return optionalSlimefunItem.get().getId().equals(identifier);
    }

    @Override
    public ItemStack asItemStack(int amount) {
        @SuppressWarnings("null")
        ItemStack itemStack = SlimefunItem.getById(identifier).getItem();
        itemStack.setAmount(amount);

        return itemStack;
    }
    
}
