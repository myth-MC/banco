package ovh.mythmc.banco.common.debug;

import java.util.Map;

import org.jetbrains.annotations.NotNull;

public interface ServerLog {

    static @NotNull ServerLogBuilder builder() {
        return new ServerLogBuilderImpl();
    }

    @NotNull String requester();

    @NotNull String pluginName();
    
    @NotNull String pluginVersion();

    int serverPort();

    @NotNull String serverVersion();

    @NotNull String serverSoftware();

    boolean serverOnlineMode();

    @NotNull Map<String, Object> extra();

}
