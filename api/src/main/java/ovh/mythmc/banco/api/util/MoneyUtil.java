package ovh.mythmc.banco.api.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.jetbrains.annotations.NotNull;

import ovh.mythmc.banco.api.Banco;

public final class MoneyUtil {

    public static BigDecimal normalize(@NotNull BigDecimal amount) {
        final int scale = Banco.get().getSettings().get().getCurrency().getScale();
        final RoundingMode roundingMode = Banco.get().getSettings().get().getCurrency().getRoundingMode();
        return amount.setScale(scale, roundingMode);
    }

    private MoneyUtil() {
        throw new UnsupportedOperationException("Utility class");
    }
    
}
