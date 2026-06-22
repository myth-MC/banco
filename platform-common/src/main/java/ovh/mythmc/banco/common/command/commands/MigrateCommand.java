package ovh.mythmc.banco.common.command.commands;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.incendo.cloud.CommandManager;
import org.incendo.cloud.description.Description;
import org.incendo.cloud.parser.flag.CommandFlag;
import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.common.command.MainCommand;
import ovh.mythmc.banco.common.command.sender.BancoCommandSource;
import ovh.mythmc.banco.common.features.MigrationFeature;
import ovh.mythmc.banco.common.util.MessageUtil;
import ovh.mythmc.gestalt.Gestalt;

public final class MigrateCommand<S extends BancoCommandSource> implements MainCommand<S> {

    @Override
    public boolean canRegister() {
        return Banco.get().getSettings().get().getMigration().isEnabled();
    }

    @Override
    public void register(@NotNull CommandManager<S> commandManager) {
        final var migrateCommand = commandManager.commandBuilder("bancomigrate")
            .permission("banco.use.bancomigrate")
            .commandDescription(Description.of("Migrates the economy from another provider to banco"));

        commandManager.command(migrateCommand
            .flag(CommandFlag.builder("confirm"))
            .handler(ctx -> {
                if (MigrationFeature.migrator().isEmpty()) {
                    MessageUtil.error(ctx.sender(), "banco.commands.bancomigrate.not-present");
                    return;
                }

                if (ctx.flags().isPresent("confirm")) {
                    MessageUtil.info(ctx.sender(), "banco.commands.bancomigrate.started");

                    MigrationFeature.migrator().get().asCompletableFuture().whenComplete((map, ex) -> {
                        if (ex != null) {
                            ex.printStackTrace(System.err);
                        }

                        map.forEach((identifierKey, balance) -> {
                            Banco.get().getLogger().info("Migrating account name {} (UUID {}) with a balance of {}...",
                                identifierKey.name(),
                                identifierKey.uuid(),
                                balance
                            );

                            Banco.get().getAccountManager().create(identifierKey.uuid(), identifierKey.name());
                            Banco.get().getAccountManager().set(identifierKey.uuid(), balance);
                        });

                        Banco.get().getLogger().warn("Migration is completed! Please disable the feature in the plugin's settings and uninstall " + MigrationFeature.migrator().get().pluginName() + ".");
                        
                        MigrationFeature.clearMigrator();
                        Gestalt.get().disableFeature(MigrationFeature.class);
                    });

                    return;
                }

                List<String> worldNameList = Banco.get().getSettings().get().getMigration().getWorlds();

                MessageUtil.info(ctx.sender(), "banco.commands.bancomigrate.checking-requirements");
                CompletableFuture.runAsync(() -> {
                    final int bancoDatabaseSize = Banco.get().getAccountManager().get().size();
                    Component databaseSizeWarning = Component.empty();
                    if (bancoDatabaseSize > 3) {
                        databaseSizeWarning = Component.text(" ").append(MessageUtil.getWarnPrefix())
                            .hoverEvent(Component.translatable("banco.commands.bancomigrate.warning.database-entries.hover"));
                    }

                    MessageUtil.warn(ctx.sender(), "banco.commands.bancomigrate.warning.1", Component.text(MigrationFeature.migrator().get().pluginName()));
                    MessageUtil.info(ctx.sender(), "banco.commands.bancomigrate.warning.2");
                    MessageUtil.info(ctx.sender(), "banco.commands.bancomigrate.warning.worlds", Component.text(worldNameList.toString(), NamedTextColor.GRAY));
                    MessageUtil.info(ctx.sender(), "banco.commands.bancomigrate.warning.database-entries", Component.text(bancoDatabaseSize, NamedTextColor.GRAY).append(databaseSizeWarning));
                    MessageUtil.info(ctx.sender(), "banco.commands.bancomigrate.warning.confirm", Component.text("/bancomigrate --confirm", NamedTextColor.GRAY));
                });
            })
        );
    }
    
}
