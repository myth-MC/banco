package ovh.mythmc.banco.api.items.impl;

import java.math.BigDecimal;

import org.bukkit.inventory.ItemStack;

import com.nexomc.nexo.api.NexoItems;

import ovh.mythmc.banco.api.items.BancoItem;

public record NexoBancoItem(String identifier, BigDecimal value) implements BancoItem {

    @Override
    public ItemStack asItemStack(int amount) {
        ItemStack itemStack = NexoItems.itemFromId(identifier).build();
        itemStack.setAmount(amount);

        return itemStack;
    }
    
}
