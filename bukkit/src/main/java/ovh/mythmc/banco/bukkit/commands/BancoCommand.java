package ovh.mythmc.banco.bukkit.commands;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.bukkit.commands.banco.*;
import ovh.mythmc.banco.common.util.MessageUtil;
import ovh.mythmc.banco.bukkit.BancoBukkit;
import ovh.mythmc.banco.common.util.UpdateChecker;

import java.util.*;
import java.util.function.BiConsumer;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

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
            String version = Banco.get().version();
            String latest = UpdateChecker.getLatest();
            Audience audience = BancoBukkit.adventure().sender(sender);

            MessageUtil.info(audience, translatable("banco.commands.banco", text(version), text("bukkit")));
            if (!version.equals(latest)) {
                MessageUtil.info(audience, translatable("banco.commands.banco.new-version", text(latest))
                        .clickEvent(ClickEvent.openUrl("https://github.com/myth-MC/banco/releases/tag/v" + latest)));
            }

            MessageUtil.debug(audience, translatable("banco.commands.banco.debug.1",
                    text(org.bukkit.Bukkit.getBukkitVersion())
            ));

            MessageUtil.debug(audience, translatable("banco.commands.banco.debug.2",
                    text(Bukkit.getServer().getOnlineMode())
            ));

            MessageUtil.debug(audience, translatable("banco.commands.banco.debug.3",
                    text(Banco.get().getEconomyManager().get().size()),
                    text("PLACEHOLDER"),
                    text(Banco.get().getAccountManager().get().size())
            ));

            if (Banco.get().getSettings().get().isDebug()) {
                MessageUtil.debug(audience, translatable("banco.commands.banco.debug-info",
                        text(Banco.get().getAccountManager().get().size()),
                        text(Banco.get().getEconomyManager().get().size())
                ));
            }

            return true;
        }

        var command = subCommands.get(args[0]);
        if (command == null) {
            MessageUtil.error(BancoBukkit.adventure().sender(sender), "banco.errors.invalid-command");
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
