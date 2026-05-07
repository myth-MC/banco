package ovh.mythmc.banco.api.items.impl;

import java.math.BigDecimal;
import java.util.Objects;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import com.nexomc.nexo.api.NexoItems;

import net.kyori.adventure.text.Component;
import ovh.mythmc.banco.api.items.BancoItem;

public record NexoBancoItem(String identifier, BigDecimal value) implements BancoItem {

    @Override
    public Component displayName() {
        return NexoItems.itemFromId(identifier).getCustomName();
    }

    @Override
    public ItemStack asItemStack(int amount) {
        ItemStack itemStack = NexoItems.itemFromId(identifier).build();
        itemStack.setAmount(amount);

        return itemStack;
    }

    @Override
    public boolean match(@NotNull ItemStack itemStack) {
        return Objects.equals(NexoItems.idFromItem(itemStack), identifier);
    }
    
}
