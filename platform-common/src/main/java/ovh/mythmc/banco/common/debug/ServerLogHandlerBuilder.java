package ovh.mythmc.banco.common.debug;

import org.jetbrains.annotations.NotNull;

import ovh.mythmc.banco.common.debug.parser.ServerLogResponseParser;
import ovh.mythmc.banco.common.debug.provider.ServerLogProvider;
import ovh.mythmc.banco.common.debug.serializer.ServerLogSerializer;
import ovh.mythmc.banco.common.debug.transport.ServerLogTransport;

public interface ServerLogHandlerBuilder {
    
    @NotNull ServerLogHandlerBuilder provider(@NotNull ServerLogProvider provider);

    @NotNull ServerLogHandlerBuilder serializer(@NotNull ServerLogSerializer serializer);

    @NotNull ServerLogHandlerBuilder transport(@NotNull ServerLogTransport transport);
    
    @NotNull ServerLogHandlerBuilder responseParser(@NotNull ServerLogResponseParser responseParser);

    @NotNull ServerLogHandler build();

    default @NotNull ServerLogHandlerBuilder provider(@NotNull ServerLog log) {
        return provider(ignored -> log);
    }

    default @NotNull ServerLogHandlerBuilder provider(@NotNull ServerLogBuilder logBuilder) {
        return provider(logBuilder.build());
    }

}
