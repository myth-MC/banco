package ovh.mythmc.banco.commands.banco;

import org.bukkit.command.CommandSender;
import ovh.mythmc.banco.Banco;
import ovh.mythmc.banco.utils.MessageUtils;
import ovh.mythmc.banco.utils.PlayerUtils;

import java.util.function.BiConsumer;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

public class GiveSubcommand implements BiConsumer<CommandSender, String[]> {

    @Override
    public void accept(CommandSender sender, String[] args) {
        if (args.length < 2) {
            MessageUtils.error(sender, "banco.errors.not-enough-arguments");
            return;
        }

        var target = Banco.get().getAccountManager().getAccount(PlayerUtils.getUuid(args[0]));
        if (target == null) {
            MessageUtils.error(sender, translatable("banco.errors.player-not-found", text(args[0])));
            return;
        }

        if (!isParsable(args[1])) {
            MessageUtils.error(sender, translatable("banco.errors.invalid-value", text(args[1])));
            return;
        }

        int amount = Integer.parseInt(args[1]);
        Banco.get().getAccountManager().add(target.getUuid(), amount);
        MessageUtils.success(sender, translatable("banco.commands.banco.give.success",
                        text(args[0]),
                        text(amount),
                        text(Banco.get().getConfig().getString("currency.symbol")))
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