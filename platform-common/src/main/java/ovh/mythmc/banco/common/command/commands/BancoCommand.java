package ovh.mythmc.banco.common.command.commands;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.component.DefaultValue;
import org.incendo.cloud.description.Description;
import org.incendo.cloud.minecraft.extras.MinecraftHelp;
import org.incendo.cloud.parser.flag.CommandFlag;
import org.incendo.cloud.parser.standard.DoubleParser;
import org.incendo.cloud.parser.standard.StringParser;
import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.accounts.Account;
import ovh.mythmc.banco.api.scheduler.BancoScheduler;
import ovh.mythmc.banco.api.util.ItemUtil;
import ovh.mythmc.banco.common.boot.BancoBootstrap;
import ovh.mythmc.banco.common.command.MainCommand;
import ovh.mythmc.banco.common.command.parser.AccountParser;
import ovh.mythmc.banco.common.command.sender.BancoCommandSource;
import ovh.mythmc.banco.common.update.UpdateChecker;
import ovh.mythmc.banco.common.util.MessageUtil;

public final class BancoCommand implements MainCommand {

    private MinecraftHelp<BancoCommandSource> help;

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public void register(@NotNull CommandManager<BancoCommandSource> commandManager) {
        // Create MinecraftHelp instance
        help = MinecraftHelp.createNative("/banco help", commandManager);

        final var bancoCommand = commandManager.commandBuilder("banco")
            .commandDescription(Description.of("Main command for managing accounts"));


        // /banco help
        commandManager.command(bancoCommand
            .literal("help", "?")
            .permission("banco.use.banco.help")
            .commandDescription(Description.of("Displays the plugin's command structure"))
            .optional("query", StringParser.greedyStringParser(), DefaultValue.constant(""))
            .handler(ctx -> {
                help.queryCommands(ctx.get("query"), ctx.sender());
            })
        );

        // /banco encode
        commandManager.command(bancoCommand
            .literal("encode", "base64")
            .permission("banco.use.banco.encode")
            .commandDescription(Description.of("Encodes the item in your main hand into a Base64 string"))
            .handler(ctx -> {
                if (!ctx.sender().isPlayer()) {
                    return;
                }

                final var player = (Player) ctx.sender().source();
                final ItemStack itemStack = player.getInventory().getItemInMainHand();

                try {
                    final String encodedString = ItemUtil.toBase64(itemStack);
                    MessageUtil.success(ctx.sender(), Component.translatable("banco.commands.encode.success",
                        Component.text(encodedString.substring(0, 16) + "...", NamedTextColor.GRAY))
                            .hoverEvent(HoverEvent.showText(Component.translatable("banco.commands.encode.hover")))
                            .clickEvent(ClickEvent.copyToClipboard(encodedString)
                        )
                    );
                } catch (IOException e) {
                    MessageUtil.error(ctx.sender(), Component.text(e.getMessage(), NamedTextColor.RED));
                }
            })  
        );

        // /banco give
        commandManager.command(bancoCommand
            .literal("give")
            .permission("banco.use.banco.give")
            .commandDescription(Description.of("Adds funds to an account"))
            .required("target", AccountParser.accountParser())
            .required("amount", DoubleParser.doubleParser(0))
            .handler(ctx -> {
                final Account target = ctx.get("target");
                final BigDecimal amount = BigDecimal.valueOf((double) ctx.get("amount"));

                Banco.get().getAccountManager().deposit(target, amount);
                MessageUtil.success(ctx.sender(), Component.translatable("banco.commands.banco.give.success",
                    Component.text(target.getName()),
                    Component.text(MessageUtil.format(amount)),
                    Component.text(Banco.get().getSettings().get().getCurrency().getSymbol()))
                );
            })
        );

        // /banco set
        commandManager.command(bancoCommand
            .literal("set")
            .permission("banco.use.banco.set")
            .commandDescription(Description.of("Sets an account's balance"))
            .required("target", AccountParser.accountParser())
            .required("amount", DoubleParser.doubleParser(0))
            .handler(ctx -> {
                final Account target = ctx.get("target");
                final BigDecimal amount = BigDecimal.valueOf((double) ctx.get("amount"));

                Banco.get().getAccountManager().set(target, amount);
                MessageUtil.success(ctx.sender(), Component.translatable("banco.commands.banco.set.success",
                    Component.text(target.getName()),
                    Component.text(MessageUtil.format(amount)),
                    Component.text(Banco.get().getSettings().get().getCurrency().getSymbol()))
                );
            })
        );

        // /banco take
        commandManager.command(bancoCommand
            .literal("take")
            .permission("banco.use.banco.take")
            .commandDescription(Description.of("Takes funds from an account"))
            .required("target", AccountParser.accountParser())
            .required("amount", DoubleParser.doubleParser(0))
            .handler(ctx -> {
                final Account target = ctx.get("target");
                final BigDecimal amount = BigDecimal.valueOf((double) ctx.get("amount"));

                final BigDecimal accountAmount = target.amount();
                if (!Banco.get().getSettings().get().getCurrency().isNegativeBalance() && amount.compareTo(accountAmount) > 0) {
                    MessageUtil.error(ctx.sender(), Component.translatable("banco.commands.banco.take.amount-too-high",
                        Component.text(MessageUtil.format(amount)),
                        Component.text(Banco.get().getSettings().get().getCurrency().getSymbol()),
                        Component.text(MessageUtil.format(accountAmount))
                    ));
                    return;
                } 

                Banco.get().getAccountManager().withdraw(target, amount);
                MessageUtil.success(ctx.sender(), Component.translatable("banco.commands.banco.take.success",
                    Component.text(target.getName()),
                    Component.text(MessageUtil.format(amount)),
                    Component.text(Banco.get().getSettings().get().getCurrency().getSymbol()))
                );
            })
        );

        // /banco dump
        commandManager.command(bancoCommand
            .literal("dump")
            .permission("banco.use.banco.dump")
            .commandDescription(Description.of("Dumps the current database into an additional file"))
            .handler(ctx -> {
                final var dateFormat = new SimpleDateFormat("MMddyyyyHHmm");
                final var dateAsString = dateFormat.format(new Date());
                Banco.get().getAccountManager().getDatabase().backup(dateAsString);

                MessageUtil.debug(ctx.sender(), "Database has been dumped into accounts.db." + dateAsString + "!");
            })
        );

        // /banco info
        commandManager.command(bancoCommand
            .literal("info")
            .permission("banco.use.banco.info")
            .commandDescription(Description.of("Displays information about the plugin"))
            .flag(CommandFlag.builder("text"))
            .handler(ctx -> {
                if (!ctx.sender().isPlayer() || ctx.flags().contains("text")) {
                    final String version = Banco.get().version();
                    final String latest = UpdateChecker.getLatest();

                    MessageUtil.info(ctx.sender(), Component.translatable("banco.commands.banco", Component.text(version), Component.text(getBancoBuildSoftware())));
                    if (!version.equals(latest)) {
                        MessageUtil.info(ctx.sender(), Component.translatable("banco.commands.banco.new-version", Component.text(latest))
                                .clickEvent(ClickEvent.openUrl("https://github.com/myth-MC/banco/releases/tag/" + latest)));
                    }

                    MessageUtil.debug(ctx.sender(), Component.translatable("banco.commands.banco.debug.1",
                        Component.text(org.bukkit.Bukkit.getBukkitVersion())
                    ));

                    MessageUtil.debug(ctx.sender(), Component.translatable("banco.commands.banco.debug.2",
                        Component.text(Bukkit.getServer().getOnlineMode())
                    ));

                    MessageUtil.debug(ctx.sender(), Component.translatable("banco.commands.banco.debug.3",
                        Component.text(Banco.get().getItemRegistry().get().size()),
                        Component.text(Banco.get().getStorageRegistry().get().size()),
                        Component.text(Banco.get().getAccountManager().get().size()),
                        Component.text(Banco.get().getAccountManager().getDatabase().getCachedAccounts().size()),
                        Component.text(BancoScheduler.get().getQueuedTransactions().size())
                    ));
                } else {
                    ((BancoBootstrap) Banco.get()).menuDispatcher().showInfo((Player) ctx.sender().source());
                }
            })
        );

        // /banco reload
        commandManager.command(bancoCommand
            .literal("reload")
            .permission("banco.use.banco.reload")
            .commandDescription(Description.of("Reloads the plugin's configuration"))
            .handler(ctx -> {
                MessageUtil.info(ctx.sender(), "banco.commands.banco.reload");
                Banco.get().reload();
                MessageUtil.success(ctx.sender(), "banco.commands.banco.reload.success");
            })
        );
    }

    private static String getBancoBuildSoftware() {
        try {
            Class.forName("ovh.mythmc.banco.paper.BancoPaperPlugin");
            return "paper";
        } catch (ClassNotFoundException ignored) {
        }

        return "bukkit";
    }
    
}
