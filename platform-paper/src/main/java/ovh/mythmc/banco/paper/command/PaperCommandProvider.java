package ovh.mythmc.banco.paper.command;

import java.util.Collection;
import java.util.List;

import org.incendo.cloud.paper.PaperCommandManager;

import ovh.mythmc.banco.common.command.BancoCommandProvider;
import ovh.mythmc.banco.common.command.MainCommand;
import ovh.mythmc.banco.paper.PaperCommandSource;
import ovh.mythmc.banco.paper.command.commands.BalanceConvertCommand;
import ovh.mythmc.banco.paper.command.commands.TransactionHistoryCommand;

public final class PaperCommandProvider extends BancoCommandProvider<PaperCommandSource> {

    private final Collection<MainCommand<PaperCommandSource>> platformCommands = List.of(
        new BalanceConvertCommand<>(),
        new TransactionHistoryCommand<>()
    );

    public PaperCommandProvider(PaperCommandManager<PaperCommandSource> commandManager) {
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
