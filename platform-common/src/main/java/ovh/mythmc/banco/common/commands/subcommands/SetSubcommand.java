package ovh.mythmc.banco.common.commands.subcommands;

import net.kyori.adventure.audience.Audience;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.accounts.Account;
import ovh.mythmc.banco.common.util.MathUtil;
import ovh.mythmc.banco.common.util.MessageUtil;
import ovh.mythmc.banco.common.util.PlayerUtil;

import java.math.BigDecimal;
import java.util.function.BiConsumer;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

public class SetSubcommand implements BiConsumer<Audience, String[]> {

    @Override
    public void accept(Audience sender, String[] args) {
        if (args.length < 2) {
            MessageUtil.error(sender, "banco.errors.not-enough-arguments");
            return;
        }

        Account target = Banco.get().getAccountManager().get(args[0]);
        if (target == null) {
            MessageUtil.error(sender, translatable("banco.errors.player-not-found", text(args[0])));
            return;
        }

        if (!MathUtil.isDouble(args[1])) {
            MessageUtil.error(sender, translatable("banco.errors.invalid-value", text(args[1])));
            return;
        }

        BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(args[1]));
        Banco.get().getAccountManager().set(target, amount);
        MessageUtil.success(sender, translatable("banco.commands.banco.set.success",
                text(args[0]),
                text(MessageUtil.format(amount)),
                text(Banco.get().getSettings().get().getCurrency().getSymbol()))
        );
    }

}