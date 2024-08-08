package ovh.mythmc.banco.paper.commands.banco;

import org.bukkit.command.CommandSender;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.economy.accounts.Account;
import ovh.mythmc.banco.common.util.MathUtil;
import ovh.mythmc.banco.common.util.MessageUtil;
import ovh.mythmc.banco.common.util.PlayerUtil;

import java.math.BigDecimal;
import java.util.function.BiConsumer;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

public class TakeSubcommand implements BiConsumer<CommandSender, String[]> {

    @Override
    public void accept(CommandSender sender, String[] args) {
        if (args.length < 2) {
            MessageUtil.error(sender, "banco.errors.not-enough-arguments");
            return;
        }

        Account target = Banco.get().getAccountManager().get(PlayerUtil.getUuid(args[0]));
        if (target == null) {
            MessageUtil.error(sender, translatable("banco.errors.player-not-found", text(args[0])));
            return;
        }

        if (!MathUtil.isDouble(args[1])) {
            MessageUtil.error(sender, translatable("banco.errors.invalid-value", text(args[1])));
            return;
        }

        BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(args[1]));
        Banco.get().getAccountManager().withdraw(target, amount);
        MessageUtil.success(sender, translatable("banco.commands.banco.take.success",
                text(args[0]),
                text(MessageUtil.format(amount)),
                text(Banco.get().getSettings().get().getCurrency().getSymbol()))
        );
    }

}