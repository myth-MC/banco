package ovh.mythmc.banco.common.debug.provider;

import org.jetbrains.annotations.NotNull;

import ovh.mythmc.banco.common.debug.ServerLog;
import ovh.mythmc.banco.common.debug.ServerLogBuilder;

@FunctionalInterface
public interface ServerLogProvider {
    
    @NotNull ServerLog provide(@NotNull ServerLogBuilder builder);

}
