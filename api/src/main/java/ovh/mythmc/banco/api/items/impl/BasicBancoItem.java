package ovh.mythmc.banco.api.items.impl;

import java.math.BigDecimal;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import ovh.mythmc.banco.api.items.BancoItem;
import ovh.mythmc.banco.api.items.BancoItemOptions;

public record BasicBancoItem(@NotNull Material material, @NotNull BigDecimal value, BancoItemOptions options) implements BancoItem {
    
}
