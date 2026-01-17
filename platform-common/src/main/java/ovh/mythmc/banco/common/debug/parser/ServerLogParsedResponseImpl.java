package ovh.mythmc.banco.common.debug.parser;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import ovh.mythmc.banco.common.debug.transport.ServerLogTransportResponse;

final class ServerLogParsedResponseImpl implements ServerLogParsedResponse {

    private final ServerLogTransportResponse raw;

    private final String logId;

    ServerLogParsedResponseImpl(ServerLogTransportResponse raw, String logId) {
        this.raw = raw;
        this.logId = logId;
    }

    @Override
    public @NotNull ServerLogTransportResponse raw() {
        return this.raw;
    }

    @Override
    public @NotNull Optional<String> logId() {
        return Optional.ofNullable(this.logId);
    }

    @Override
    public boolean isSuccess() {
        return raw.statusCode() >= 200 && raw.statusCode() < 300
            && logId != null;
    }
    
}
