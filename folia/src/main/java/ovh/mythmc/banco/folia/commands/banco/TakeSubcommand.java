package ovh.mythmc.banco.folia.commands.banco;

import org.bukkit.command.CommandSender;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.economy.Account;
import ovh.mythmc.banco.common.util.MessageUtil;
import ovh.mythmc.banco.common.util.PlayerUtil;

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

        if (!isParsable(args[1])) {
            MessageUtil.error(sender, translatable("banco.errors.invalid-value", text(args[1])));
            return;
        }

        int amount = Integer.parseInt(args[1]);
        Banco.get().getAccountManager().withdraw(target, amount);
        MessageUtil.success(sender, translatable("banco.commands.banco.take.success",
                text(args[0]),
                text(amount),
                text(Banco.get().getConfig().getSettings().getCurrency().getString("symbol")))
        );
    }

    private boolean isParsable(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (final NumberFormatException e) {
            return false;
        }
    }

}