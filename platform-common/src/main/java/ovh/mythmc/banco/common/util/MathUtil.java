package ovh.mythmc.banco.common.util;

import org.jetbrains.annotations.NotNull;

public final class MathUtil {

    public static boolean isDouble(final @NotNull String input) {
        try {
            Double.parseDouble(input);
            return true;
        } catch (final NumberFormatException e) {
            return false;
        }
    }

}
