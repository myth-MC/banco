package ovh.mythmc.banco.common.debug;

import org.jetbrains.annotations.NotNull;

import ovh.mythmc.banco.common.debug.parser.ServerLogParsedResponse;
import ovh.mythmc.banco.common.debug.parser.ServerLogResponseParser;
import ovh.mythmc.banco.common.debug.provider.ServerLogProvider;
import ovh.mythmc.banco.common.debug.serializer.ServerLogSerializer;
import ovh.mythmc.banco.common.debug.transport.ServerLogTransport;
import ovh.mythmc.banco.common.debug.transport.ServerLogTransportResponse;

final class ServerLogHandlerImpl implements ServerLogHandler {

    private final ServerLogProvider provider;

    private final ServerLogSerializer serializer;

    private final ServerLogTransport transport;

    private final ServerLogResponseParser responseParser;

    ServerLogHandlerImpl(
        ServerLogProvider provider,
        ServerLogSerializer serializer,
        ServerLogTransport transport,
        ServerLogResponseParser responseParser
    ) {
        this.provider = provider;
        this.serializer = serializer;
        this.transport = transport;
        this.responseParser = responseParser;
    }

    @Override
    public @NotNull ServerLogHandleResult handle() {
        try {
            final ServerLog log = provider.provide(ServerLog.builder());
            final byte[] payload = serializer.serialize(log);
            final ServerLogTransportResponse response = transport.send(serializer.contentType(), payload);
            final ServerLogParsedResponse parsedResponse = responseParser.parse(response);

            return ServerLogHandleResult.success(parsedResponse, null);
        } catch (Exception e) {
            return ServerLogHandleResult.failure(e.getMessage());
        }
    }
    
}
