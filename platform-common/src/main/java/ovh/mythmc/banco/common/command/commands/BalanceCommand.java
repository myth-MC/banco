package ovh.mythmc.banco.common.command.commands;

import org.incendo.cloud.CommandManager;
import org.incendo.cloud.description.Description;
import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.text.Component;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.accounts.Account;
import ovh.mythmc.banco.common.command.MainCommand;
import ovh.mythmc.banco.common.command.parser.AccountParser;
import ovh.mythmc.banco.common.command.sender.BancoCommandSource;
import ovh.mythmc.banco.common.util.MessageUtil;

public final class BalanceCommand implements MainCommand {

    @Override
    public boolean canRegister() {
        return Banco.get().getSettings().get().getCommands().getBalance().enabled();
    }

    @Override
    public void register(@NotNull CommandManager<BancoCommandSource> commandManager) {
        final var balanceCommand = commandManager.commandBuilder("balance", "bal", "money")
            .permission("banco.use.balance")
            .commandDescription(Description.of("Allows players to view their current balance"));

        commandManager.command(balanceCommand
            .optional("account", AccountParser.accountParser())
            .handler(ctx -> {
                if (!ctx.contains("account") && !ctx.sender().isPlayer()) {
                    // console?
                    return;
                }

                final Account account = ctx.getOrDefault("account", Banco.get().getAccountManager().getByName(ctx.sender().name()));
                if (!ctx.contains("account")) {
                    MessageUtil.info(ctx.sender(), Component.translatable("banco.commands.balance",
                        Component.text(MessageUtil.format(account.amount())),
                        Component.text(Banco.get().getSettings().get().getCurrency().getSymbol()))
                    );
                } else {
                    MessageUtil.info(ctx.sender(), Component.translatable("banco.commands.balance.others",
                        Component.text(account.getName()),
                        Component.text(MessageUtil.format(account.amount())),
                        Component.text(Banco.get().getSettings().get().getCurrency().getSymbol()))
                    );
                }
            })
        );
    }
    
}
