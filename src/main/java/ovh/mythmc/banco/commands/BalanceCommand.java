package ovh.mythmc.banco.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ovh.mythmc.banco.Banco;
import ovh.mythmc.banco.commands.banco.ReloadSubcommand;
import ovh.mythmc.banco.economy.Account;
import ovh.mythmc.banco.utils.MessageUtils;
import ovh.mythmc.banco.utils.PlayerUtils;

import java.util.*;
import java.util.function.BiConsumer;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

public class BalanceCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player)) return true;

        if (args.length == 0) {
            MessageUtils.info(sender, translatable("banco.commands.balance",
                    text(Banco.get().getAccountManager().getActualAmount((OfflinePlayer) sender)),
                    text(Banco.get().getConfig().getString("currency.symbol")))
            );
            return true;
        }

        Account target = Banco.get().getAccountManager().getAccount(PlayerUtils.getUuid(args[0]));

        if (target == null) {
            return true;
        }

        MessageUtils.info(sender, translatable("banco.commands.balance.others",
                text(Bukkit.getOfflinePlayer(target.getUuid()).getName()),
                text(Banco.get().getAccountManager().getActualAmount(target.getUuid())),
                text(Banco.get().getConfig().getString("currency.symbol")))
        );
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
