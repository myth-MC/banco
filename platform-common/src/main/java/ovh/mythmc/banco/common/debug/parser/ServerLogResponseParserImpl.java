package ovh.mythmc.banco.common.debug.parser;

import org.jetbrains.annotations.NotNull;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import ovh.mythmc.banco.common.debug.transport.ServerLogTransportResponse;

final class ServerLogResponseParserImpl implements ServerLogResponseParser {

    @Override
    public @NotNull ServerLogParsedResponse parse(@NotNull ServerLogTransportResponse response) {
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            return ServerLogParsedResponse.empty(response);
        }

        final String body = response.body();
        if (body == null || body.isBlank()) {
            return ServerLogParsedResponse.empty(response);
        }

        final JsonObject json = JsonParser.parseString(body).getAsJsonObject();

        if (!json.has("logId")) {
            return ServerLogParsedResponse.empty(response);
        }

        return ServerLogParsedResponse.of(response, json.get("logId").getAsString());
    }
    
}
