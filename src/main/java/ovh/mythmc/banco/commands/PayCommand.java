package ovh.mythmc.banco.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ovh.mythmc.banco.Banco;
import ovh.mythmc.banco.utils.MessageUtils;
import ovh.mythmc.banco.utils.PlayerUtils;

import java.util.ArrayList;
import java.util.List;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

public class PayCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player)) return true;

        var source = Banco.get().getAccountManager().getAccount(((Player) sender).getUniqueId());

        if (args.length < 2) {
            MessageUtils.error(sender, "banco.errors.not-enough-arguments");
            return true;
        }

        var target = Banco.get().getAccountManager().getAccount(PlayerUtils.getUuid(args[0]));
        if (target == null) {
            MessageUtils.error(sender, translatable("banco.errors.player-not-found", text(args[0])));
            return true;
        }

        if (target.equals(source)) {
            MessageUtils.error(sender, "banco.commands.pay.cannot-send-money-to-yourself");
            return true;
        }

        if (!isParsable(args[1])) {
            MessageUtils.error(sender, translatable("banco.errors.invalid-value", text(args[1])));
            return true;
        }

        int amount = Integer.parseInt(args[1]);
        if (Banco.get().getAccountManager().getActualAmount(source.getUuid()) < amount) {
            MessageUtils.error(sender, "banco.errors.not-enough-funds");
            return true;
        }

        Banco.get().getAccountManager().remove(source.getUuid(), amount);
        Banco.get().getAccountManager().add(target.getUuid(), amount);

        MessageUtils.success(sender, translatable("banco.commands.pay.success",
                text(amount),
                text(Banco.get().getConfig().getString("currency.symbol")),
                text(Bukkit.getOfflinePlayer(target.getUuid()).getName()))
        );

        if (Bukkit.getOfflinePlayer(target.getUuid()).isOnline()) {
            MessageUtils.info(Bukkit.getOfflinePlayer(target.getUuid()).getPlayer(), translatable("banco.commands.pay.received",
                    text(Bukkit.getOfflinePlayer(source.getUuid()).getName()),
                    text(amount),
                    text(Banco.get().getConfig().getString("currency.symbol"))
            ));
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        if (args.length > 1)
            return List.of();

        List<String> onlinePlayers = new ArrayList<>();
        Bukkit.getOnlinePlayers().forEach(player -> onlinePlayers.add(player.getName()));
        return List.copyOf(onlinePlayers);
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
