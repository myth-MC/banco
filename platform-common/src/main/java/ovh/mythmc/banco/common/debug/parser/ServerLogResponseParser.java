package ovh.mythmc.banco.common.debug.parser;

import org.jetbrains.annotations.NotNull;

import ovh.mythmc.banco.common.debug.transport.ServerLogTransportResponse;

public interface ServerLogResponseParser {

    static @NotNull ServerLogResponseParser get() {
        return new ServerLogResponseParserImpl();
    }
    
    @NotNull ServerLogParsedResponse parse(@NotNull ServerLogTransportResponse response);

}
