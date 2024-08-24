package ovh.mythmc.banco.common.commands;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.accounts.Account;
import ovh.mythmc.banco.common.util.MathUtil;
import ovh.mythmc.banco.common.util.MessageUtil;
import ovh.mythmc.banco.common.util.PlayerUtil;

import java.math.BigDecimal;
import java.util.*;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

public abstract class PayCommand {

    public void run(@NotNull Audience sender, @NotNull String[] args) {
        Optional<UUID> uuid = sender.get(Identity.UUID);
        if (uuid.isEmpty()) return;
        Account source = Banco.get().getAccountManager().get(uuid.get());

        if (args.length < 2) {
            MessageUtil.error(sender, "banco.errors.not-enough-arguments");
            return;
        }

        Account target = Banco.get().getAccountManager().get(PlayerUtil.getUuid(args[0]));
        if (target == null) {
            MessageUtil.error(sender, translatable("banco.errors.player-not-found", text(args[0])));
            return;
        }

        if (target.equals(source)) {
            MessageUtil.error(sender, "banco.commands.pay.cannot-send-money-to-yourself");
            return;
        }

        if (!MathUtil.isDouble(args[1])) {
            MessageUtil.error(sender, translatable("banco.errors.invalid-value", text(args[1])));
            return;
        }

        BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(args[1]));
        if (Banco.get().getAccountManager().amount(source).compareTo(amount) < 0) {
            MessageUtil.error(sender, "banco.errors.not-enough-funds");
            return;
        }

        Banco.get().getAccountManager().withdraw(source, amount);
        Banco.get().getAccountManager().deposit(target, amount);

        MessageUtil.success(sender, translatable("banco.commands.pay.success",
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

    public @NotNull Collection<String> getSuggestions(@NotNull String[] args) {
        if (args.length > 0)
            return List.of();

        List<String> onlinePlayers = new ArrayList<>();
        Bukkit.getOnlinePlayers().forEach(player -> onlinePlayers.add(player.getName()));
        return List.copyOf(onlinePlayers);
    }

}
