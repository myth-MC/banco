package ovh.mythmc.banco.common.command.commands;

import java.math.BigDecimal;

import org.incendo.cloud.CommandManager;
import org.incendo.cloud.description.Description;
import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.accounts.Account;
import ovh.mythmc.banco.common.command.MainCommand;
import ovh.mythmc.banco.common.command.sender.BancoCommandSource;

public final class BalanceChangeCommand implements MainCommand {

    @Override
    public boolean canRegister() {
        return Banco.get().getSettings().get().getCommands().getBalanceChange().enabled();
    }

    @Override
    public void register(@NotNull CommandManager<BancoCommandSource> commandManager) {
        final var balanceChangeCommand = commandManager.commandBuilder("balancechange", "balchange")
            .permission("banco.use.balancechange")
            .commandDescription(Description.of("Allows players to compact their balance"));

        commandManager.command(balanceChangeCommand
            .handler(ctx -> {
                if (!ctx.sender().isPlayer()) {
                    // console?
                    return;
                }

                final Account account = Banco.get().getAccountManager().getByName(ctx.sender().name());
                final BigDecimal amount = account.amount();

                ctx.sender().playSound(Sound.sound(Key.key("item.armor.equip_iron"), Sound.Source.PLAYER, 0.65F, 1.50F));
                
                Banco.get().getAccountManager().withdraw(account, amount);
                Banco.get().getAccountManager().deposit(account, amount);
            })
        );
    }
    
}
