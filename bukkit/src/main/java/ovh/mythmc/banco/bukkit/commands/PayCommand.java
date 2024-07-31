package ovh.mythmc.banco.bukkit.commands;

import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.economy.accounts.Account;
import ovh.mythmc.banco.bukkit.BancoBukkit;
import ovh.mythmc.banco.common.util.MathUtil;
import ovh.mythmc.banco.common.util.MessageUtil;
import ovh.mythmc.banco.common.util.PlayerUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

public class PayCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player)) return true;

        Account source = Banco.get().getAccountManager().get(((Player) sender).getUniqueId());

        if (args.length < 2) {
            MessageUtil.error(BancoBukkit.adventure().sender(sender), "banco.errors.not-enough-arguments");
            return true;
        }

        Account target = Banco.get().getAccountManager().get(PlayerUtil.getUuid(args[0]));
        if (target == null) {
            MessageUtil.error(BancoBukkit.adventure().sender(sender), translatable("banco.errors.player-not-found", text(args[0])));
            return true;
        }

        if (target.equals(source)) {
            MessageUtil.error(BancoBukkit.adventure().sender(sender), "banco.commands.pay.cannot-send-money-to-yourself");
            return true;
        }

        if (!MathUtil.isDouble(args[1])) {
            MessageUtil.error(BancoBukkit.adventure().sender(sender), translatable("banco.errors.invalid-value", text(args[1])));
            return true;
        }

        BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(args[1]));
        if (Banco.get().getAccountManager().amount(source).compareTo(amount) < 0) {
            MessageUtil.error(BancoBukkit.adventure().sender(sender), "banco.errors.not-enough-funds");
            return true;
        }

        Banco.get().getAccountManager().withdraw(source, amount);
        Banco.get().getAccountManager().deposit(target, amount);

        MessageUtil.success(BancoBukkit.adventure().sender(sender), translatable("banco.commands.pay.success",
                text(MessageUtil.format(amount)),
                text(Banco.get().getConfig().getSettings().getCurrency().symbol()),
                text(Bukkit.getOfflinePlayer(target.getUuid()).getName()))
        );

        if (Bukkit.getOfflinePlayer(target.getUuid()).isOnline()) {
            MessageUtil.info((Audience) Bukkit.getOfflinePlayer(target.getUuid()).getPlayer(), translatable("banco.commands.pay.received",
                    text(Bukkit.getOfflinePlayer(source.getUuid()).getName()),
                    text(MessageUtil.format(amount)),
                    text(Banco.get().getConfig().getSettings().getCurrency().symbol())
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

}
