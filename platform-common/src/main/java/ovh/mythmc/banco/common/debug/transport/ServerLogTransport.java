package ovh.mythmc.banco.common.debug.transport;

import org.jetbrains.annotations.NotNull;

public interface ServerLogTransport {
    
    @NotNull ServerLogTransportResponse send(@NotNull String contentType, byte[] payload) throws Exception;

}
