package ovh.mythmc.banco.paper.commands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.common.util.MessageUtil;
import ovh.mythmc.banco.common.util.UpdateChecker;
import ovh.mythmc.banco.paper.commands.banco.*;

import java.util.*;
import java.util.function.BiConsumer;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

@SuppressWarnings("UnstableApiUsage")
public final class BancoCommand implements BasicCommand {

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
    public void execute(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        if (args.length == 0) {
            String version = Banco.get().version();
            String latest = UpdateChecker.getLatest();

            MessageUtil.info(stack.getSender(), translatable("banco.commands.banco", text(version), text("paper")));
            if (!version.equals(latest)) {
                MessageUtil.info(stack.getSender(), translatable("banco.commands.banco.new-version", text(latest))
                        .clickEvent(ClickEvent.openUrl("https://github.com/myth-MC/banco/releases/tag/v" + latest)));
            }

            MessageUtil.debug(stack.getSender(), translatable("banco.commands.banco.debug.1",
                    text(org.bukkit.Bukkit.getVersionMessage().substring(23))
            ));

            MessageUtil.debug(stack.getSender(), translatable("banco.commands.banco.debug.2",
                    text(Bukkit.getServer().getOnlineMode())
            ));

            MessageUtil.debug(stack.getSender(), translatable("banco.commands.banco.debug.3",
                    text(Banco.get().getEconomyManager().get().size()),
                    text("PLACEHOLDER"),
                    text(Banco.get().getAccountManager().get().size())
            ));

            if (Banco.get().getSettings().get().isDebug()) {
                MessageUtil.debug(stack.getSender(), translatable("banco.commands.banco.debug-info",
                        text(Banco.get().getAccountManager().get().size()),
                        text(Banco.get().getEconomyManager().get().size())
                ));
            }

            return;
        }

        var command = subCommands.get(args[0]);
        if (command == null) {
            MessageUtil.error(stack.getSender(), "banco.errors.invalid-command");
            return;
        }

        command.accept(stack.getSender(), Arrays.copyOfRange(args, 1, args.length));
    }

    @Override
    public @NotNull Collection<String> suggest(@NotNull CommandSourceStack commandSourceStack, @NotNull String[] args) {
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

    @Override
    public String permission() {
        return "banco.admin";
    }

}
