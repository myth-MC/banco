package ovh.mythmc.banco.common.debug;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

final class ServerLogBuilderImpl implements ServerLogBuilder {

    private String requester;

    private String pluginName;

    private String pluginVersion;

    private int serverPort;

    private String serverVersion;

    private String serverSoftware;

    private boolean onlineMode;

    private final Map<String, Object> extra = new HashMap<>();

    @Override
    public @NotNull ServerLogBuilder requester(@NotNull String requester) {
        this.requester = requester;
        return this;
    }

    @Override
    public @NotNull ServerLogBuilder pluginName(@NotNull String pluginName) {
        this.pluginName = pluginName;
        return this;
    }

    @Override
    public @NotNull ServerLogBuilder pluginVersion(@NotNull String pluginVersion) {
        this.pluginVersion = pluginVersion;
        return this;
    }

    @Override
    public @NotNull ServerLogBuilder serverPort(int serverPort) {
        this.serverPort = serverPort;
        return this;
    }

    @Override
    public @NotNull ServerLogBuilder serverVersion(@NotNull String serverVersion) {
        this.serverVersion = serverVersion;
        return this;
    }

    @Override
    public @NotNull ServerLogBuilder serverSoftware(@NotNull String serverSoftware) {
        this.serverSoftware = serverSoftware;
        return this;
    }

    @Override
    public @NotNull ServerLogBuilder onlineMode(boolean onlineMode) {
        this.onlineMode = onlineMode;
        return this;
    }

    @Override
    public @NotNull ServerLogBuilder addExtra(@NotNull String key, @NotNull Object value) {
        this.extra.put(key, value);
        return this;
    }

    @Override
    public @NotNull ServerLog build() {
        return new ServerLogImpl(requester, pluginName, pluginVersion, serverPort, serverVersion, serverSoftware, onlineMode, extra);
    }
    
}
