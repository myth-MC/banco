package ovh.mythmc.banco.common.debug;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import ovh.mythmc.banco.common.debug.parser.ServerLogParsedResponse;

final class ServerLogHandleResultImpl implements ServerLogHandleResult {

    private final boolean success;

    private final ServerLogParsedResponse parsedResponse;

    private final String message;

    ServerLogHandleResultImpl(boolean success, ServerLogParsedResponse parsedResponse, String message) {
        this.success = success;
        this.parsedResponse = parsedResponse;
        this.message = message;
    }

    @Override
    public @NotNull Optional<ServerLogParsedResponse> parsedResponse() {
        return Optional.ofNullable(this.parsedResponse);
    }

    @Override
    public @NotNull Optional<String> message() {
        return Optional.ofNullable(this.message);
    }

    @Override
    public boolean isSuccessful() {
        return this.success;
    }

}
