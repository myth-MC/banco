package ovh.mythmc.banco.paper.commands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.economy.accounts.Account;
import ovh.mythmc.banco.common.util.MessageUtil;
import ovh.mythmc.banco.common.util.PlayerUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

@SuppressWarnings("UnstableApiUsage")
public final class BalanceCommand implements BasicCommand {
    @Override
    public void execute(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        if (args.length == 0) {
            if (!(stack.getSender() instanceof Player)) return;

            BigDecimal amount = Banco.get().getAccountManager().get(((Player) stack.getSender()).getUniqueId()).amount();

            MessageUtil.info(stack.getSender(), translatable("banco.commands.balance",
                    text(MessageUtil.format(amount)),
                    text(Banco.get().getConfig().getSettings().getCurrency().symbol()))
            );
            return;
        }

        Account target = Banco.get().getAccountManager().get(PlayerUtil.getUuid(args[0]));

        if (target == null) {
            MessageUtil.error(stack.getSender(), translatable("banco.errors.player-not-found", text(args[0])));
            return;
        }

        BigDecimal amount = target.amount();

        MessageUtil.info(stack.getSender(), translatable("banco.commands.balance.others",
                text(Bukkit.getOfflinePlayer(target.getUuid()).getName()),
                text(MessageUtil.format(amount)),
                text(Banco.get().getConfig().getSettings().getCurrency().symbol()))
        );
    }

    @Override
    public @NotNull Collection<String> suggest(@NotNull CommandSourceStack commandSourceStack, @NotNull String[] args) {
        if (args.length > 0)
            return List.of();

        List<String> onlinePlayers = new ArrayList<>();
        Bukkit.getOnlinePlayers().forEach(player -> onlinePlayers.add(player.getName()));
        return List.copyOf(onlinePlayers);
    }

    @Override
    public String permission() {
        return "banco.user";
    }

}
