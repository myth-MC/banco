package ovh.mythmc.banco.common.commands;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.accounts.Account;
import ovh.mythmc.banco.common.util.MessageUtil;

import java.math.BigDecimal;
import java.util.*;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

public abstract class BalanceCommand {

    public void run(@NotNull Audience sender, @NotNull String[] args) {
        Optional<UUID> uuid = sender.get(Identity.UUID);

        if (args.length == 0) {
            if (uuid.isEmpty()) return;

            BigDecimal amount = Banco.get().getAccountManager().getByUuid(uuid.get()).amount();

            MessageUtil.info(sender, translatable("banco.commands.balance",
                    text(MessageUtil.format(amount)),
                    text(Banco.get().getSettings().get().getCurrency().getSymbol()))
            );
            return;
        }

        if (args[0].equalsIgnoreCase("change")) {
            if (uuid.isEmpty()) return;
            if (!Banco.get().getSettings().get().getCurrency().isChangeMoney())
                return;

            Account account = Banco.get().getAccountManager().getByUuid(uuid.get());
            if (account == null)
                return;

            BigDecimal toRemove = account.amount();

            Player player = Bukkit.getPlayer(uuid.get());
            player.playSound(player, Sound.ITEM_ARMOR_EQUIP_IRON, 0.95F, 1.50F);

            Banco.get().getAccountManager().withdraw(account, toRemove);
            Banco.get().getAccountManager().deposit(account, toRemove);
            return;
        }

        Account target = Banco.get().getAccountManager().getByName(args[0]);

        if (target == null) {
            MessageUtil.error(sender, translatable("banco.errors.player-not-found", text(args[0])));
            return;
        }

        BigDecimal amount = target.amount();

        MessageUtil.info(sender, translatable("banco.commands.balance.others",
                text(args[0]),
                text(MessageUtil.format(amount)),
                text(Banco.get().getSettings().get().getCurrency().getSymbol()))
        );
    }

    public @NotNull Collection<String> getSuggestions(@NotNull String[] args) {
        if (args.length > 1)
            return List.of();

        List<String> onlinePlayers = new ArrayList<>();
        Bukkit.getOnlinePlayers().forEach(player -> onlinePlayers.add(player.getName()));
        if (Banco.get().getSettings().get().getCurrency().isChangeMoney())
            onlinePlayers.add("change");
        return List.copyOf(onlinePlayers);
    }

}
