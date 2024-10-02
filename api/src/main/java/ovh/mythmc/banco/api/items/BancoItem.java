package ovh.mythmc.banco.api.items;

import org.jetbrains.annotations.NotNull;

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
public record BancoItem(@NotNull String name,
                        String displayName,
                        List<String> lore,
                        Integer customModelData,
                        Boolean glowEffect,
                        @NotNull BigDecimal value) { }