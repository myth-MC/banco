package ovh.mythmc.banco.common.debug.parser;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import ovh.mythmc.banco.common.debug.transport.ServerLogTransportResponse;

public interface ServerLogParsedResponse {

    static @NotNull ServerLogParsedResponse of(@NotNull ServerLogTransportResponse raw, String logId) {
        return new ServerLogParsedResponseImpl(raw, logId);
    }

    static @NotNull ServerLogParsedResponse empty(@NotNull ServerLogTransportResponse raw) {
        return of(raw, null);
    }

    @NotNull ServerLogTransportResponse raw();

    @NotNull Optional<String> logId();

    boolean isSuccess();

}
