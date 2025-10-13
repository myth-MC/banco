package ovh.mythmc.banco.common.command;

import org.incendo.cloud.CommandManager;
import org.jetbrains.annotations.NotNull;

import ovh.mythmc.banco.common.command.sender.BancoCommandSource;

public interface MainCommand {

    boolean canRegister();

    void register(@NotNull CommandManager<BancoCommandSource> commandManager);
    
}
