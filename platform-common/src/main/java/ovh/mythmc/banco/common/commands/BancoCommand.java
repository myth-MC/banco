package ovh.mythmc.banco.common.commands;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.banco.common.commands.subcommands.*;
import ovh.mythmc.banco.common.util.MessageUtil;

import java.util.*;
import java.util.function.BiConsumer;

public abstract class BancoCommand {

    private final Map<String, BiConsumer<Audience, String[]>> subCommands;

    public BancoCommand() {
        this.subCommands = new HashMap<>();
        subCommands.put("info", new InfoSubcommand());
        subCommands.put("set", new SetSubcommand());
        subCommands.put("give", new GiveSubcommand());
        subCommands.put("take", new TakeSubcommand());
        subCommands.put("reload", new ReloadSubcommand());
        subCommands.put("save", new SaveSubcommand());
    }

    public void run(@NotNull Audience sender, @NotNull String[] args) {
        if (args.length == 0) {
            Optional<UUID> uuid = sender.get(Identity.UUID);
            if (uuid.isEmpty())
                return;

            Bukkit.getPlayer(uuid.get()).performCommand("banco info");
            return;
        }

        var command = subCommands.get(args[0]);
        if (command == null) {
            MessageUtil.error(sender, "banco.errors.invalid-command");
            return;
        }

        command.accept(sender, Arrays.copyOfRange(args, 1, args.length));
    }

    public @NotNull Collection<String> getSuggestions(@NotNull String[] args) {
        if (args.length == 1) {
            return List.copyOf(subCommands.keySet());
        }

        if (args.length == 2) {
            switch (args[0]) {
                case "give", "take", "set":
                    List<String> onlinePlayers = new ArrayList<>();
                    Bukkit.getOnlinePlayers().forEach(player -> onlinePlayers.add(player.getName()));
                    return List.copyOf(onlinePlayers);
                case "info":
                    return List.of("dump");
            }
        }

        return List.of();
    }

}
