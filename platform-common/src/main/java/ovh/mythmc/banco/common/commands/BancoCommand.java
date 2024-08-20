package ovh.mythmc.banco.common.commands;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.common.commands.subcommands.*;
import ovh.mythmc.banco.common.util.MessageUtil;
import ovh.mythmc.banco.common.util.UpdateChecker;

import java.util.*;
import java.util.function.BiConsumer;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

public abstract class BancoCommand {

    private final Map<String, BiConsumer<Audience, String[]>> subCommands;

    public BancoCommand() {
        this.subCommands = new HashMap<>();
        subCommands.put("set", new SetSubcommand());
        subCommands.put("give", new GiveSubcommand());
        subCommands.put("take", new TakeSubcommand());
        subCommands.put("reload", new ReloadSubcommand());
        subCommands.put("save", new SaveSubcommand());
    }

    public void run(@NotNull Audience sender, @NotNull String[] args) {
        if (args.length == 0) {
            String version = Banco.get().version();
            String latest = UpdateChecker.getLatest();

            MessageUtil.info(sender, translatable("banco.commands.banco", text(version), text(getBancoBuildSoftware())));
            if (!version.equals(latest)) {
                MessageUtil.info(sender, translatable("banco.commands.banco.new-version", text(latest))
                        .clickEvent(ClickEvent.openUrl("https://github.com/myth-MC/banco/releases/tag/v" + latest)));
            }

            MessageUtil.debug(sender, translatable("banco.commands.banco.debug.1",
                    text(org.bukkit.Bukkit.getBukkitVersion())
            ));

            MessageUtil.debug(sender, translatable("banco.commands.banco.debug.2",
                    text(Bukkit.getServer().getOnlineMode())
            ));

            MessageUtil.debug(sender, translatable("banco.commands.banco.debug.3",
                    text(Banco.get().getItemManager().get().size()),
                    text(Banco.get().getInventoryManager().get().size()),
                    text(Banco.get().getAccountManager().get().size())
            ));

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
            switch (args[0]) {
                case "give", "take", "set":
                    List<String> onlinePlayers = new ArrayList<>();
                    Bukkit.getOnlinePlayers().forEach(player -> onlinePlayers.add(player.getName()));
                    return List.copyOf(onlinePlayers);
            }
        }

        if (args.length > 1)
            return List.of();

        return List.copyOf(subCommands.keySet());
    }

    private String getBancoBuildSoftware() {
        try {
            Class.forName("ovh.mythmc.banco.paper.BancoPaperPlugin");
            return "paper";
        } catch (ClassNotFoundException ignored) {
        }

        return "bukkit";
    }

}
