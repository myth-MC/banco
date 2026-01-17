package ovh.mythmc.banco.common.debug.serializer;

import java.nio.charset.StandardCharsets;

import org.jetbrains.annotations.NotNull;

import com.google.gson.Gson;

import ovh.mythmc.banco.common.debug.ServerLog;

public final class GsonServerLogSerializer implements ServerLogSerializer {

    public static GsonServerLogSerializer get() {
        return new GsonServerLogSerializer();
    }

    private final Gson gson = new Gson();

    private GsonServerLogSerializer() { }
    
    @Override
    public @NotNull String contentType() {
        return "application/json";
    }

    @Override
    public @NotNull byte[] serialize(@NotNull ServerLog log) throws Exception {
        return gson.toJson(log).getBytes(StandardCharsets.UTF_8);
    }

}
