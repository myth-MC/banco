package ovh.mythmc.banco.common.debug.transport;

final class ServerLogTransportResponseImpl implements ServerLogTransportResponse {

    private final int statusCode;

    private final String body;

    ServerLogTransportResponseImpl(int statusCode, String body) {
        this.statusCode = statusCode;
        this.body = body;
    }

    @Override
    public int statusCode() {
        return this.statusCode;
    }

    @Override
    public String body() {
        return this.body;
    }
    
}
