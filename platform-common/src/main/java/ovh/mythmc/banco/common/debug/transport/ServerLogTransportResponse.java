package ovh.mythmc.banco.common.debug.transport;

import org.jetbrains.annotations.NotNull;

public interface ServerLogTransportResponse {

    static @NotNull ServerLogTransportResponse of(int statusCode, String body) {
        return new ServerLogTransportResponseImpl(statusCode, body);
    }

    int statusCode();

    String body();
    
}
