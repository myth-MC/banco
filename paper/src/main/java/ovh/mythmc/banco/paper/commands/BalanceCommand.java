package ovh.mythmc.banco.paper.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.economy.Account;
import ovh.mythmc.banco.common.util.MessageUtil;
import ovh.mythmc.banco.common.util.PlayerUtil;

import java.util.*;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

public class BalanceCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player)) return true;
            MessageUtil.info(sender, translatable("banco.commands.balance",
                    text(Banco.get().getAccountManager().get(((Player) sender).getUniqueId()).amount()),
                    text(Banco.get().getConfig().getSettings().getCurrency().getString("symbol")))
            );
            return true;
        }

        Account target = Banco.get().getAccountManager().get(PlayerUtil.getUuid(args[0]));

        if (target == null) {
            MessageUtil.error(sender, translatable("banco.errors.player-not-found", text(args[0])));
            return true;
        }

        MessageUtil.info(sender, translatable("banco.commands.balance.others",
                text(Bukkit.getOfflinePlayer(target.getUuid()).getName()),
                text(Banco.get().getAccountManager().get(((Player) sender).getUniqueId()).amount()),
                text(Banco.get().getConfig().getSettings().getCurrency().getString("symbol")))
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
