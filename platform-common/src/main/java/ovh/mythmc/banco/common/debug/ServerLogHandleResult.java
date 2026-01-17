package ovh.mythmc.banco.common.debug;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import ovh.mythmc.banco.common.debug.parser.ServerLogParsedResponse;

public interface ServerLogHandleResult {

    static @NotNull ServerLogHandleResult success(@NotNull ServerLogParsedResponse parsedResponse, String message) {
        return new ServerLogHandleResultImpl(true, parsedResponse, message);
    }

    static @NotNull ServerLogHandleResult failure(String message) {
        return new ServerLogHandleResultImpl(false, null, message);
    }

    @NotNull Optional<ServerLogParsedResponse> parsedResponse();

    @NotNull Optional<String> message();

    boolean isSuccessful();

    default void ifSuccess(@NotNull Runnable runnable) {
        if (isSuccessful())
            runnable.run();
    }

    default void ifFailure(@NotNull Runnable runnable) {
        if (!isSuccessful())
            runnable.run();
    }

    default void ifSuccessOrElse(@NotNull Runnable success, @NotNull Runnable failure) {
        if (isSuccessful()) {
            success.run();
        } else {
            failure.run();
        }
    }
    
}
