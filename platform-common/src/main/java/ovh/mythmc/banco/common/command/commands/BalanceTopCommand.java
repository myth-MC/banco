package ovh.mythmc.banco.common.command.commands;

import org.bukkit.entity.Player;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.description.Description;
import org.jetbrains.annotations.NotNull;

import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.common.boot.BancoBootstrap;
import ovh.mythmc.banco.common.command.MainCommand;
import ovh.mythmc.banco.common.command.sender.BancoCommandSource;

public final class BalanceTopCommand<S extends BancoCommandSource> implements MainCommand<S> {

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public void register(@NotNull CommandManager<S> commandManager) {
        final var balanceTopCommand = commandManager.commandBuilder("balancetop", "baltop")
            .permission("banco.use.balancetop")
            .commandDescription(Description.of("Displays the balance top"));
        
        commandManager.command(balanceTopCommand
            .handler(ctx -> {
                if (!ctx.sender().isPlayer()) {
                    // console?
                    return;
                }

                ((BancoBootstrap) Banco.get()).menuDispatcher().showBalanceTop((Player) ctx.sender().source());
                //MenuManager.getInstance().openInventory(new BalanceTopMenu(), (Player) ctx.sender().source());
            })
        );
    }
    
}
