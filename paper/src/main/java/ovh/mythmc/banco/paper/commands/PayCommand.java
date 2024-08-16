package ovh.mythmc.banco.paper.commands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.accounts.Account;
import ovh.mythmc.banco.common.util.MathUtil;
import ovh.mythmc.banco.common.util.MessageUtil;
import ovh.mythmc.banco.common.util.PlayerUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

@SuppressWarnings("UnstableApiUsage")
public class PayCommand implements BasicCommand {

    @Override
    public void execute(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        if (!(stack.getSender() instanceof Player)) return;

        Account source = Banco.get().getAccountManager().get(PlayerUtil.getUuid(stack.getSender().getName()));

        if (args.length < 2) {
            MessageUtil.error(stack.getSender(), "banco.errors.not-enough-arguments");
            return;
        }

        Account target = Banco.get().getAccountManager().get(PlayerUtil.getUuid(args[0]));
        if (target == null) {
            MessageUtil.error(stack.getSender(), translatable("banco.errors.player-not-found", text(args[0])));
            return;
        }

        if (target.equals(source)) {
            MessageUtil.error(stack.getSender(), "banco.commands.pay.cannot-send-money-to-yourself");
            return;
        }

        if (!MathUtil.isDouble(args[1])) {
            MessageUtil.error(stack.getSender(), translatable("banco.errors.invalid-value", text(args[1])));
            return;
        }

        BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(args[1]));
        if (Banco.get().getAccountManager().amount(source).compareTo(amount) < 0) {
            MessageUtil.error(stack.getSender(), "banco.errors.not-enough-funds");
            return;
        }

        Banco.get().getAccountManager().withdraw(source, amount);
        Banco.get().getAccountManager().deposit(target, amount);

        MessageUtil.success(stack.getSender(), translatable("banco.commands.pay.success",
                text(MessageUtil.format(amount)),
                text(Banco.get().getSettings().get().getCurrency().getSymbol()),
                text(Bukkit.getOfflinePlayer(target.getUuid()).getName()))
        );

        if (Bukkit.getOfflinePlayer(target.getUuid()).isOnline()) {
            MessageUtil.info((Audience) Bukkit.getOfflinePlayer(target.getUuid()).getPlayer(), translatable("banco.commands.pay.received",
                    text(Bukkit.getOfflinePlayer(source.getUuid()).getName()),
                    text(MessageUtil.format(amount)),
                    text(Banco.get().getSettings().get().getCurrency().getSymbol())
            ));
        }
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
