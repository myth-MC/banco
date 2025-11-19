package ovh.mythmc.banco.paper.command.commands;

import org.bukkit.entity.Player;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.description.Description;
import org.jetbrains.annotations.NotNull;

import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.accounts.Account;
import ovh.mythmc.banco.common.boot.BancoBootstrap;
import ovh.mythmc.banco.common.command.MainCommand;
import ovh.mythmc.banco.common.command.parser.AccountParser;
import ovh.mythmc.banco.common.command.sender.BancoCommandSource;

public final class TransactionHistoryCommand implements MainCommand {

    @Override
    public boolean canRegister() {
        return Banco.get().getSettings().get().getCommands().getTransactions().enabled();
    }

    @Override
    public void register(@NotNull CommandManager<BancoCommandSource> commandManager) {
        final var balanceTopCommand = commandManager.commandBuilder("transactions", "th")
            .permission("banco.use.transactions")
            .commandDescription(Description.of("Displays the transaction history"));
        
        commandManager.command(balanceTopCommand
            .optional("account", AccountParser.accountParser())
            .handler(ctx -> {
                if (!ctx.sender().isPlayer()) {
                    // console?
                    return;
                }

                final Account account = ctx.getOrDefault("account", Banco.get().getAccountManager().getByName(ctx.sender().name()));
                ((BancoBootstrap) Banco.get()).menuDispatcher().showTransactionHistory((Player) ctx.sender().source(), account);
            })
        );
    }
    
}
