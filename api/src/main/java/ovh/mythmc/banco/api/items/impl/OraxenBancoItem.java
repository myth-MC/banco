package ovh.mythmc.banco.api.items.impl;

import java.math.BigDecimal;
import java.util.Objects;

import org.bukkit.inventory.ItemStack;

import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.items.ItemBuilder;
import net.kyori.adventure.text.Component;
import ovh.mythmc.banco.api.items.BancoItem;

public record OraxenBancoItem(String identifier, BigDecimal value) implements BancoItem {

    @Override
    public Component displayName() {
        return Component.text(getItemBuilder().getDisplayName());
    }

    @Override
    public boolean match(ItemStack itemStack) {
        return Objects.equals(identifier, OraxenItems.getIdByItem(itemStack));
    }

    @Override
    public ItemStack asItemStack(int amount) {
        ItemStack itemStack = getItemBuilder().build();
        itemStack.setAmount(amount);
        return itemStack;
    }

    private ItemBuilder getItemBuilder() {
        return OraxenItems.getItemById(identifier);
    }
    
}
