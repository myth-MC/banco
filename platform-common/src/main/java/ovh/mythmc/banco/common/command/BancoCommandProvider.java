package ovh.mythmc.banco.common.command;

import java.util.Collection;
import java.util.List;

import org.incendo.cloud.CommandManager;
import org.incendo.cloud.caption.CaptionProvider;

import ovh.mythmc.banco.common.command.commands.BalanceCommand;
import ovh.mythmc.banco.common.command.commands.BalanceTopCommand;
import ovh.mythmc.banco.common.command.commands.BancoCommand;
import ovh.mythmc.banco.common.command.commands.MigrateCommand;
import ovh.mythmc.banco.common.command.commands.PayCommand;
import ovh.mythmc.banco.common.command.sender.BancoCommandSource;

public abstract class BancoCommandProvider<S extends BancoCommandSource> {

    protected final CommandManager<S> commandManager;

    private final Collection<MainCommand<S>> commands = List.of(
        new BalanceCommand<>(),
        new BalanceTopCommand<>(),
        new BancoCommand<>(),
        new MigrateCommand<>(),
        new PayCommand<>()
    );

    public BancoCommandProvider(final CommandManager<S> commandManager) {
        this.commandManager = commandManager;
    }

    public void register() {
        // Register captions
        commandManager.captionRegistry().registerProvider(
            CaptionProvider.constantProvider(BancoCaptionKeys.ARGUMENT_PARSE_FAILURE_ACCOUNT, "Could not find any account matching '<input>'")
        );

        // Register commands
        this.commands.forEach(command -> {
            if (command.canRegister())
                command.register(commandManager);
        });

        // Register platform commands
        registerPlatformCommands();
    }

    public void registerPlatformCommands() {
    }
    
}
