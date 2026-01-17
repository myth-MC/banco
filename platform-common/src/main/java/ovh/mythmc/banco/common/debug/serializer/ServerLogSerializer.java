package ovh.mythmc.banco.common.debug.serializer;

import org.jetbrains.annotations.NotNull;

import ovh.mythmc.banco.common.debug.ServerLog;

public interface ServerLogSerializer {
    
    @NotNull String contentType();

    @NotNull byte[] serialize(@NotNull ServerLog log) throws Exception;

}
