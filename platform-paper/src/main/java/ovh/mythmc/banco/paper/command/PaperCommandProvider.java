package ovh.mythmc.banco.paper.command;

import java.util.Collection;
import java.util.List;

import org.incendo.cloud.paper.LegacyPaperCommandManager;

import ovh.mythmc.banco.common.command.BancoCommandProvider;
import ovh.mythmc.banco.common.command.MainCommand;
import ovh.mythmc.banco.common.command.sender.BancoCommandSource;
import ovh.mythmc.banco.paper.command.commands.TransactionHistoryCommand;

public final class PaperCommandProvider extends BancoCommandProvider {

    private final Collection<MainCommand> platformCommands = List.of(
        new TransactionHistoryCommand()
    );

    public PaperCommandProvider(LegacyPaperCommandManager<BancoCommandSource> commandManager) {
        super(commandManager);
    }

    @Override
    public void registerPlatformCommands() {
        platformCommands.forEach(platformCommand -> {
            if (platformCommand.canRegister())
                platformCommand.register(commandManager);
        });
    }
    
}
