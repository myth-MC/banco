package ovh.mythmc.banco.common.debug;

import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.NotNull;

public interface ServerLogHandler {

    static @NotNull ServerLogHandlerBuilder builder() {
        return new ServerLogHandlerBuilderImpl();
    }

    @NotNull ServerLogHandleResult handle();

    default @NotNull CompletableFuture<ServerLogHandleResult> handleAsync() {
        return CompletableFuture.completedFuture(handle());
    }
    
}
