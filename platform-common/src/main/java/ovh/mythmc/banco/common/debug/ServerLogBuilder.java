package ovh.mythmc.banco.common.debug;

import org.jetbrains.annotations.NotNull;

public interface ServerLogBuilder {

    @NotNull ServerLogBuilder requester(@NotNull String requester); 

    @NotNull ServerLogBuilder pluginName(@NotNull String pluginName);

    @NotNull ServerLogBuilder pluginVersion(@NotNull String pluginVersion);

    @NotNull ServerLogBuilder serverPort(int serverPort);

    @NotNull ServerLogBuilder serverVersion(@NotNull String serverVersion);

    @NotNull ServerLogBuilder serverSoftware(@NotNull String serverSoftware);

    @NotNull ServerLogBuilder onlineMode(boolean onlineMode);

    @NotNull ServerLogBuilder addExtra(@NotNull String key, @NotNull Object value);

    @NotNull ServerLog build();
    
}
