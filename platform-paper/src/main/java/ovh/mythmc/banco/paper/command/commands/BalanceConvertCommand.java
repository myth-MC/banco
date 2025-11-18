package ovh.mythmc.banco.paper.command.commands;

import org.bukkit.entity.Player;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.description.Description;
import org.jetbrains.annotations.NotNull;

import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.common.boot.BancoBootstrap;
import ovh.mythmc.banco.common.command.MainCommand;
import ovh.mythmc.banco.common.command.sender.BancoCommandSource;

public final class BalanceConvertCommand implements MainCommand {

    @Override
    public boolean canRegister() {
        return Banco.get().getSettings().get().getCommands().getBalanceConvert().enabled();
    }

    @Override
    public void register(@NotNull CommandManager<BancoCommandSource> commandManager) {
        final var balanceConvertCommand = commandManager.commandBuilder("balanceconvert", "balconvert")
            .permission("banco.use.balanceconvert")
            .commandDescription(Description.of("Allows players to convert their balance into an equivalent amount of a specific item"));

        commandManager.command(balanceConvertCommand
            .handler(ctx -> {
                if (!ctx.sender().isPlayer()) {
                    // console?
                    return;
                }

                ((BancoBootstrap) Banco.get()).menuDispatcher().showItemConverter((Player) ctx.sender().source());
            })
        );
    }
    
}
