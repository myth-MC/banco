package ovh.mythmc.banco.api.items.impl;

import java.math.BigDecimal;

import org.bukkit.inventory.ItemStack;

import ovh.mythmc.banco.api.items.BancoItem;
import xyz.xenondevs.nova.api.Nova;
import xyz.xenondevs.nova.api.item.NovaItem;

public record NovaBancoItem(String identifier, BigDecimal value) implements BancoItem {

    @Override
    public ItemStack asItemStack(int amount) {
        return getNovaItem().createItemStack(amount);
    }

    private NovaItem getNovaItem() {
        Nova nova = Nova.getNova();
        return nova.getItemRegistry().get(identifier);
    }
    
}
