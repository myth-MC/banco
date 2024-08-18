package ovh.mythmc.banco.api.items;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record BancoItem(@NotNull String name,
                        String displayName,
                        List<String> lore,
                        Integer customModelData,
                        @NotNull BigDecimal value) { }