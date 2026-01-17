package ovh.mythmc.banco.common.debug.transport;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.jetbrains.annotations.NotNull;

public final class HttpServerLogTransport implements ServerLogTransport {

    private static final URI endpoint = URI.create("https://debug.mythmc.ovh/api/v1/upload");

    public static HttpServerLogTransport get() {
        return new HttpServerLogTransport();
    }

    private final HttpClient client = HttpClient.newHttpClient();

    private HttpServerLogTransport() { }

    @Override
    public ServerLogTransportResponse send(@NotNull String contentType, byte[] payload) throws Exception {

        final HttpRequest request = HttpRequest.newBuilder(endpoint)
            .header("Content-Type", contentType)
            .POST(HttpRequest.BodyPublishers.ofByteArray(payload))
            .build();

        final HttpResponse<String> response =
            client.send(request, HttpResponse.BodyHandlers.ofString());

        return ServerLogTransportResponse.of(response.statusCode(), response.body());
    }
    
}
