package ovh.mythmc.banco.common.debug;

import org.jetbrains.annotations.NotNull;

import ovh.mythmc.banco.common.debug.parser.ServerLogResponseParser;
import ovh.mythmc.banco.common.debug.provider.ServerLogProvider;
import ovh.mythmc.banco.common.debug.serializer.ServerLogSerializer;
import ovh.mythmc.banco.common.debug.transport.ServerLogTransport;

final class ServerLogHandlerBuilderImpl implements ServerLogHandlerBuilder {

    private ServerLogProvider provider;

    private ServerLogSerializer serializer;

    private ServerLogTransport transport;

    private ServerLogResponseParser responseParser;

    @Override
    public @NotNull ServerLogHandlerBuilder provider(@NotNull ServerLogProvider provider) {
        this.provider = provider;
        return this;
    }

    @Override
    public @NotNull ServerLogHandlerBuilder serializer(@NotNull ServerLogSerializer serializer) {
        this.serializer = serializer;
        return this;
    }

    @Override
    public @NotNull ServerLogHandlerBuilder transport(@NotNull ServerLogTransport transport) {
        this.transport = transport;
        return this;
    }

    @Override
    public @NotNull ServerLogHandlerBuilder responseParser(@NotNull ServerLogResponseParser responseParser) {
        this.responseParser = responseParser;
        return this;
    }

    @Override
    public @NotNull ServerLogHandler build() {
        return new ServerLogHandlerImpl(provider, serializer, transport, responseParser);
    } 
    
}
