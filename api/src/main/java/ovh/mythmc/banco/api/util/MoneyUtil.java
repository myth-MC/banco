package ovh.mythmc.banco.api.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.configuration.sections.CurrencyConfig;

public final class MoneyUtil {

    private static MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    public static BigDecimal normalize(@NotNull BigDecimal amount) {
        final int scale = Banco.get().getSettings().get().getCurrency().getScale();
        final RoundingMode roundingMode = Banco.get().getSettings().get().getCurrency().getRoundingMode();
        return amount.setScale(scale, roundingMode);
    }

    public static Component prefix() {
        return MINI_MESSAGE.deserialize(Banco.get().getSettings().get().getCurrency().getPrefix());
    }

    public static Component suffix() {
        return MINI_MESSAGE.deserialize(Banco.get().getSettings().get().getCurrency().getSuffix());
    }

    @SuppressWarnings("deprecation")
    public static Component format(@NotNull BigDecimal amount) {
        final DecimalFormat format = new DecimalFormat(Banco.get().getSettings().get().getCurrency().getFormat());
        final String formattedAmount = format.format(amount);

        final CurrencyConfig currencyConfig = Banco.get().getSettings().get().getCurrency();
        if (currencyConfig.getSymbol() != null) {
            return Component.text(formattedAmount)
                .append(MINI_MESSAGE.deserialize(currencyConfig.getSymbol()));
        }

        return prefix()
            .append(Component.text(formattedAmount))
            .append(suffix());
    }

    private MoneyUtil() {
        throw new UnsupportedOperationException("Utility class");
    }
    
}
