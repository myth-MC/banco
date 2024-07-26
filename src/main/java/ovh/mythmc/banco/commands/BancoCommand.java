package ovh.mythmc.banco.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ovh.mythmc.banco.commands.banco.LoadSubcommand;
import ovh.mythmc.banco.commands.banco.ReloadSubcommand;
import ovh.mythmc.banco.commands.banco.SaveSubcommand;
import ovh.mythmc.banco.utils.MessageUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class BancoCommand implements CommandExecutor, TabCompleter {

    private final Map<String, BiConsumer<CommandSender, String[]>> subCommands;

    public BancoCommand() {
        this.subCommands = new HashMap<>();
        subCommands.put("reload", new ReloadSubcommand());
        subCommands.put("save", new SaveSubcommand());
        subCommands.put("load", new LoadSubcommand());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        if (args.length == 0) {
            MessageUtils.error(sender, "banco.errors.not-enough-arguments");
            return true;
        }

        var command = subCommands.get(args[0]);
        if (command == null) {
            MessageUtils.error(sender, "banco.errors.invalid-command");
            return true;
        }

        command.accept(sender, Arrays.copyOfRange(args, 1, args.length));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        if (args.length > 1)
            return List.of();
        return List.copyOf(subCommands.keySet());
    }

}
