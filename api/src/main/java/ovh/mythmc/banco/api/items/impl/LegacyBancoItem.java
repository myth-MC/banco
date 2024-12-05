package ovh.mythmc.banco.api.items.impl;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import lombok.AccessLevel;
import lombok.With;
import ovh.mythmc.banco.api.items.BancoItem;
import ovh.mythmc.banco.api.items.BancoItemOptions;

import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @param name a case-sensitive material's name, for example GOLD_INGOT
 * @param displayName this item's display name
 * @param lore this item's lore text
 * @param customModelData this item's custom model data
 * @param glowEffect whether item should glow or not
 * @param value value of this item
 */
@With(AccessLevel.PACKAGE)
public record LegacyBancoItem(@NotNull String name,
                        String displayName,
                        List<String> lore,
                        Integer customModelData,
                        Boolean glowEffect,
                        @NotNull BigDecimal value) implements BancoItem {
                            
    @Override
    public Material material() {
        return Material.valueOf(name);
    }

    @Override
    public BancoItemOptions options() {
        return new BancoItemOptions(displayName, lore, customModelData, glowEffect, null);
    } 

}