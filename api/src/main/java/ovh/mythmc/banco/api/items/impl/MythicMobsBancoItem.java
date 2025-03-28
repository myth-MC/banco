package ovh.mythmc.banco.api.items.impl;

import java.math.BigDecimal;

import org.bukkit.inventory.ItemStack;

//import io.lumine.mythic.bukkit.MythicBukkit;
import ovh.mythmc.banco.api.items.BancoItem;

public record MythicMobsBancoItem(String identifier, BigDecimal value)  {//implements BancoItem {

    //@Override
    public ItemStack asItemStack(int amount) {
        return null;
        //return MythicBukkit.inst().getItemManager().getItemStack(identifier, amount);
    }
    
}
