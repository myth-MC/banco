package ovh.mythmc.banco.common.util;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.banco.api.Banco;

import java.math.BigDecimal;
import java.text.DecimalFormat;

@SuppressWarnings("unused")
public final class MessageUtil {

    private static final class Icons {
        public static String WARNING = "\u26A0";
        public static String CHECKMARK = "\u2714";
        public static String CROSS = "\u274C";
        public static String BELL = "\uD83D\uDD14";
        public static String BUG = "\uD83E\uDEB2";
    }

    public static TextColor INFO_COLOR = TextColor.color(106, 178, 197);
    public static TextColor WARN_COLOR = TextColor.color(255, 163, 25);
    public static TextColor SUCCESS_COLOR = TextColor.color(110,188,81);
    public static TextColor ERROR_COLOR = TextColor.color(129,9,10);
    public static TextColor DEBUG_COLOR = TextColor.color(44, 200, 60);
    public static TextColor TEXT_COLOR = TextColor.color(240,239,255);

    public static void info(Audience audience, String message) {
        info(audience, Component.translatable(message));
    }

    public static void info(Audience audience, Component message) {
        audience.sendMessage(Component.translatable(Icons.BELL + " ", INFO_COLOR)
                .append(message.color(TEXT_COLOR)));
    }

    public static void warn(Audience audience, String message) {
        warn(audience, Component.translatable(message));
    }

    public static void warn(Audience audience,Component message) {
        audience.sendMessage(Component.translatable(Icons.WARNING + " ", WARN_COLOR)
                .append(message.color(TEXT_COLOR)));
    }

    public static void success(Audience audience, String message) {
        success(audience, Component.translatable(message));
    }

    public static void success(Audience audience, Component message) {
        audience.sendMessage(Component.translatable(Icons.CHECKMARK + " ", SUCCESS_COLOR)
                .append(message.color(TEXT_COLOR)));
    }

    public static void error(Audience audience, String message) {
        error(audience, Component.translatable(message));
    }

    public static void error(Audience audience, Component message) {
        audience.sendMessage(Component.translatable(Icons.CROSS + " ", ERROR_COLOR)
                .append(message.color(TEXT_COLOR)));
    }

    public static void debug(Audience audience, String message) { debug(audience, Component.translatable(message)); }

    public static void debug(Audience audience, Component message) {
        audience.sendMessage(Component.translatable(Icons.BUG + " ", DEBUG_COLOR)
                .append(message.color(TEXT_COLOR)));
    }

    public static String format(final @NotNull BigDecimal value) {
        DecimalFormat format = new DecimalFormat(Banco.get().getSettings().get().getCurrency().getFormat());
        return format.format(value);
    }

}
