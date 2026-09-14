package ovh.mythmc.banco.common.command.commands;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.description.Description;
import org.incendo.cloud.parser.flag.CommandFlag;
import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.accounts.service.OfflinePlayerReference;
import ovh.mythmc.banco.api.util.MoneyUtil;
import ovh.mythmc.banco.common.boot.BancoBootstrap;
import ovh.mythmc.banco.common.command.MainCommand;
import ovh.mythmc.banco.common.command.sender.BancoCommandSource;

public final class BalanceTopCommand<S extends BancoCommandSource> implements MainCommand<S> {

    private final static MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public void register(@NotNull CommandManager<S> commandManager) {
        final var balanceTopCommand = commandManager.commandBuilder("balancetop", "baltop")
            .permission("banco.use.balancetop")
            .commandDescription(Description.of("Displays the balance top"));
        
        commandManager.command(balanceTopCommand
            .flag(CommandFlag.builder("text").withDescription(Description.of("Shows information as text instead of opening the GUI")).build())
            .handler(ctx -> {
                if (!ctx.sender().isPlayer() || ctx.flags().hasFlag("text")) {
                    Banco.get().getAccountManager().getTopAsync(1024000).thenAccept(map -> {
                        int index = 0;
                
                        for (Map.Entry<UUID, BigDecimal> entry : map.entrySet()) {
                            if (index >= 9)
                                break;
                
                            final Optional<OfflinePlayerReference> optPlayerReference = Banco.get().getAccountManager().getUuidResolver().resolveOfflinePlayer(entry.getKey());
                            if (optPlayerReference.isEmpty() || !optPlayerReference.get().toOfflinePlayer().hasPlayedBefore())
                                continue;
                
                            final Component balance = MoneyUtil.format(entry.getValue());
                            final String name = optPlayerReference.get().toOfflinePlayer().getName();

                            final Component formattedText = MINI_MESSAGE.deserialize(
                                Banco.get().getSettings().get().getDialogs().getBalanceTop().entryFormat(),
                                Placeholder.component("position", Component.text(index+1)),
                                Placeholder.component("player_name", Component.text(name)),
                                Placeholder.component("balance", balance)
                            );
                
                            ctx.sender().sendMessage(formattedText);
                
                            index++;
                        }
                    }).exceptionally(t -> {
                        Banco.get().getLogger().error("Exception while showing balance top dialog:");
                        t.printStackTrace(System.err);
                        return null;
                    });
                    return;
                }

                ((BancoBootstrap) Banco.get()).menuDispatcher().showBalanceTop((Player) ctx.sender().source());
            })
        );
    }
    
}
