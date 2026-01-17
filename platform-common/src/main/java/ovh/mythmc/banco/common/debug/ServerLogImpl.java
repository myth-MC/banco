package ovh.mythmc.banco.common.debug;

import java.util.Map;

import org.jetbrains.annotations.NotNull;

final class ServerLogImpl implements ServerLog {

    private final String requester;

    private final String pluginName;

    private final String pluginVersion;

    private final int serverPort;

    private final String serverVersion;

    private final String serverSoftware;

    private final boolean serverOnlineMode;

    private final Map<String, Object> extra;

    ServerLogImpl(
        String requester,
        String pluginName,
        String pluginVersion,
        int serverPort,
        String serverVersion,
        String serverSoftware,
        boolean serverOnlineMode,
        Map<String, Object> extra
    ) { 
        this.requester = requester;
        this.pluginName = pluginName;
        this.pluginVersion = pluginVersion;
        this.serverPort = serverPort;
        this.serverVersion = serverVersion;
        this.serverSoftware = serverSoftware;
        this.serverOnlineMode = serverOnlineMode;
        this.extra = extra;
    }

    @Override
    public @NotNull String requester() {
        return this.requester;
    }

    @Override
    public @NotNull String pluginName() {
        return this.pluginName;
    }

    @Override
    public @NotNull String pluginVersion() {
        return this.pluginVersion;
    }

    @Override
    public int serverPort() {
        return this.serverPort;
    }

    @Override
    public @NotNull String serverVersion() {
        return this.serverVersion;
    }

    @Override
    public @NotNull String serverSoftware() {
        return this.serverSoftware;
    }

    @Override
    public boolean serverOnlineMode() {
        return this.serverOnlineMode;
    }

    @Override
    public @NotNull Map<String, Object> extra() {
        return this.extra;
    }
    
}
