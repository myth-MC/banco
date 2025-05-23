package ovh.mythmc.banco.common.command.commands;

import java.math.BigDecimal;

import org.bukkit.Bukkit;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.description.Description;
import org.incendo.cloud.parser.standard.DoubleParser;
import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.accounts.Account;
import ovh.mythmc.banco.common.command.MainCommand;
import ovh.mythmc.banco.common.command.parser.AccountParser;
import ovh.mythmc.banco.common.command.sender.BancoCommandSource;
import ovh.mythmc.banco.common.util.MessageUtil;

public final class PayCommand implements MainCommand {

    @Override
    public boolean canRegister() {
        return Banco.get().getSettings().get().getCommands().getPay().enabled();
    }

    @Override
    public void register(@NotNull CommandManager<BancoCommandSource> commandManager) {
        final var payCommand = commandManager.commandBuilder("pay")
            .permission("banco.use.pay")
            .commandDescription(Description.of("Transfers money to another player's account"));

        commandManager.command(payCommand
            .required("target", AccountParser.accountParser())
            .required("amount", DoubleParser.doubleParser(0))
            .handler(ctx -> {
                if (!ctx.sender().isPlayer()) {
                    // console?
                }

                final Account source = Banco.get().getAccountManager().getByName(ctx.sender().name());
                final Account target = ctx.get("target");
                final BigDecimal amount = BigDecimal.valueOf((double) ctx.get("amount"));

                // Cannot send money to yourself
                if (target.equals(source)) {
                    MessageUtil.error(ctx.sender(), "banco.commands.pay.cannot-send-money-to-yourself");
                    return;
                }

                // Not enough funds
                if (Banco.get().getAccountManager().amount(source).compareTo(amount) < 0) {
                    MessageUtil.error(ctx.sender(), "banco.errors.not-enough-funds");
                    return;
                }

                Banco.get().getAccountManager().withdraw(source, amount);
                Banco.get().getAccountManager().deposit(target, amount);

                MessageUtil.success(ctx.sender(), Component.translatable("banco.commands.pay.success",
                        Component.text(MessageUtil.format(amount)),
                        Component.text(Banco.get().getSettings().get().getCurrency().getSymbol()),
                        Component.text(Bukkit.getOfflinePlayer(target.getUuid()).getName()))
                );

                if (Bukkit.getOfflinePlayer(target.getUuid()).isOnline()) {
                    MessageUtil.info((Audience) Bukkit.getOfflinePlayer(target.getUuid()).getPlayer(), Component.translatable("banco.commands.pay.received",
                        Component.text(Bukkit.getOfflinePlayer(source.getUuid()).getName()),
                        Component.text(MessageUtil.format(amount)),
                        Component.text(Banco.get().getSettings().get().getCurrency().getSymbol())
                    ));
                }
            })
        );
    }
    
}
