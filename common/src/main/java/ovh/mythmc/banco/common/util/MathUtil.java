package ovh.mythmc.banco.common.util;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public final class MathUtil {

    public static <K, V extends Comparable<? super V>> LinkedHashMap<K, V> sortByValue(final @NotNull Map<K, V> map) {
        return map
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
    }

    public static boolean isDouble(final @NotNull String input) {
        try {
            Double.parseDouble(input);
            return true;
        } catch (final NumberFormatException e) {
            return false;
        }
    }

}
