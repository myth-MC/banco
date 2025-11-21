package ovh.mythmc.banco.api.logger;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;

@ApiStatus.Internal
public interface LoggerWrapper {
    // Taken from
    // https://github.com/j256/ormlite-core/blob/master/src/main/java/com/j256/ormlite/logger/Logger.java
    String ARG_STRING = "{}";
    int ARG_STRING_LENGTH = ARG_STRING.length();
    Object UNKNOWN_ARG = new Object();

    void info(final String message, final Object... args);

    void warn(final String message, final Object... args);

    void error(final String message, final Object... args);

    default void debug(final String message, final Object... args) {
        info(message, args);
    }

    default String buildFullMessage(final @NotNull String msg, final Object... args) {
        StringBuilder sb = null;
        int lastIndex = 0;
        int argC = 0;
        while (true) {
            int argIndex = msg.indexOf(ARG_STRING, lastIndex);
            // no more {} arguments?
            if (argIndex == -1) {
                break;
            }
            if (sb == null) {
                // we build this lazily in case there is no {} in the msg
                sb = new StringBuilder(128);
            }
            // add the string before the arg-string
            sb.append(msg, lastIndex, argIndex);
            // shift our last-index past the arg-string
            lastIndex = argIndex + ARG_STRING_LENGTH;
            // add the arguments
            if (argC < args.length) {
                appendArg(sb, args[argC]);
            }
            argC++;
        }
        if (sb == null) {
            return msg;
        } else {
            sb.append(msg, lastIndex, msg.length());
            return sb.toString();
        }
    }

    default void appendArg(final StringBuilder stringBuilder, final Object arg) {
        if (arg == UNKNOWN_ARG) {
            // ignore it
        } else if (arg == null) {
            stringBuilder.append("null");
        } else if (arg.getClass().isArray()) {
            stringBuilder.append('[');
            int length = Array.getLength(arg);
            for (int i = 0; i < length; i++) {
                if (i > 0) {
                    stringBuilder.append(", ");
                }
                appendArg(stringBuilder, Array.get(arg, i));
            }
            stringBuilder.append(']');
        } else {
            stringBuilder.append(arg);
        }
    }
}