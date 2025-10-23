package ovh.mythmc.banco.common.command;

import java.util.Collection;
import java.util.List;

import org.incendo.cloud.bukkit.CloudBukkitCapabilities;
import org.incendo.cloud.caption.CaptionProvider;
import org.incendo.cloud.paper.LegacyPaperCommandManager;

import ovh.mythmc.banco.common.command.commands.BalanceChangeCommand;
import ovh.mythmc.banco.common.command.commands.BalanceCommand;
import ovh.mythmc.banco.common.command.commands.BalanceTopCommand;
import ovh.mythmc.banco.common.command.commands.BancoCommand;
import ovh.mythmc.banco.common.command.commands.PayCommand;
import ovh.mythmc.banco.common.command.sender.BancoCommandSource;

public abstract class BancoCommandProvider {

    protected final LegacyPaperCommandManager<BancoCommandSource> commandManager;

    private final Collection<MainCommand> commands = List.of(
        new BalanceChangeCommand(),
        new BalanceCommand(),
        new BalanceTopCommand(),
        new BancoCommand(),
        new PayCommand()
    );

    public BancoCommandProvider(final LegacyPaperCommandManager<BancoCommandSource> commandManager) {
        this.commandManager = commandManager;
    }

    public void register() {
        // Register captions
        commandManager.captionRegistry().registerProvider(
            CaptionProvider.constantProvider(BancoCaptionKeys.ARGUMENT_PARSE_FAILURE_ACCOUNT, "Could not find any account matching '<input>'")
        );

        // Enable platform capabilities
        if (commandManager.hasCapability(CloudBukkitCapabilities.NATIVE_BRIGADIER)) {
            commandManager.registerBrigadier();
        } else if (commandManager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            commandManager.registerAsynchronousCompletions();
        }

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
