package ovh.mythmc.banco.paper.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ovh.mythmc.banco.common.util.MessageUtil;
import ovh.mythmc.banco.paper.commands.banco.*;

import java.util.*;
import java.util.function.BiConsumer;

public class BancoCommand implements CommandExecutor, TabCompleter {

    private final Map<String, BiConsumer<CommandSender, String[]>> subCommands;

    public BancoCommand() {
        this.subCommands = new HashMap<>();
        subCommands.put("set", new SetSubcommand());
        subCommands.put("give", new GiveSubcommand());
        subCommands.put("take", new TakeSubcommand());
        subCommands.put("reload", new ReloadSubcommand());
        subCommands.put("save", new SaveSubcommand());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        if (args.length == 0) {
            MessageUtil.error(sender, "banco.errors.not-enough-arguments");
            return true;
        }

        var command = subCommands.get(args[0]);
        if (command == null) {
            MessageUtil.error(sender, "banco.errors.invalid-command");
            return true;
        }

        command.accept(sender, Arrays.copyOfRange(args, 1, args.length));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        if (args.length == 2) {
            switch (args[0]) {
                case "give", "take", "set":
                    List<String> onlinePlayers = new ArrayList<>();
                    Bukkit.getOnlinePlayers().forEach(player -> onlinePlayers.add(player.getName()));
                    return List.copyOf(onlinePlayers);
            }
        }

        if (args.length > 2)
            return List.of();

        return List.copyOf(subCommands.keySet());
    }

}
