package ovh.mythmc.banco.api.items.impl;

import java.math.BigDecimal;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.text.Component;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import ovh.mythmc.banco.api.items.BancoItem;

public record CraftEngineBancoItem(String identifier, BigDecimal value) implements BancoItem {

    @Override
    public @NotNull Component displayName() {
        return asItemStack().hasItemMeta() && asItemStack().getItemMeta().hasDisplayName()
            ? Component.text(asItemStack().getItemMeta().getDisplayName())
            : Component.text(asItemStack().getItemMeta().getItemName());
    }

    @Override
    public @NotNull ItemStack asItemStack(int amount) {
        final ItemStack itemStack = CraftEngineItems.byId(identifier).buildBukkitItem();
        itemStack.setAmount(amount);
        return itemStack.clone();
    }

}
