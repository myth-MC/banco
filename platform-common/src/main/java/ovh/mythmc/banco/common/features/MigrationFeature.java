package ovh.mythmc.banco.common.features;

import java.util.List;

import org.bukkit.Bukkit;

import lombok.RequiredArgsConstructor;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.scheduler.BancoScheduler;
import ovh.mythmc.banco.common.hooks.migration.Migrator;
import ovh.mythmc.banco.common.hooks.migration.TNEMigrator;
import ovh.mythmc.gestalt.Gestalt;
import ovh.mythmc.gestalt.annotations.Feature;
import ovh.mythmc.gestalt.annotations.conditions.FeatureConditionBoolean;
import ovh.mythmc.gestalt.annotations.status.FeatureEnable;
import ovh.mythmc.gestalt.annotations.status.FeatureInitialize;

@RequiredArgsConstructor
@Feature(group = "banco", identifier = "MIGRATION")
public final class MigrationFeature {

    public static boolean ACTIVE;

    private final List<Migrator> migrators = List.of(
        new TNEMigrator()
    );

    @FeatureInitialize
    public void initialize() {
        ACTIVE = Banco.get().getSettings().get().isVaultMigration();
    }

    @FeatureConditionBoolean
    public boolean canBeEnabled() {
        return ACTIVE;
    }

    @FeatureEnable
    public void enable() {
        Banco.get().getLogger().warn("The migration feature is active. Most of the plugin's functions will be disabled.");

        // Schedule so that it runs once the server is running
        BancoScheduler.get().run(() -> {
            for (Migrator migrator : migrators) {
                if (Bukkit.getPluginManager().getPlugin(migrator.pluginName()) == null) {
                    continue;
                }

                migrator.asCompletableFuture().thenAccept(map -> {
                    map.forEach((identifierKey, balance) -> {
                        Banco.get().getLogger().debug("Migrating account name {} (UUID {}) with a balance of {}...",
                            identifierKey.name(),
                            identifierKey.uuid(),
                            balance
                        );

                        Banco.get().getAccountManager().create(identifierKey.uuid(), identifierKey.name());
                        Banco.get().getAccountManager().set(identifierKey.uuid(), balance);
                    });
                });
            } 

            Banco.get().getLogger().warn("Migration has been completed! Please disable the feature in the plugin's settings and restart the server.");
            Gestalt.get().disableAllFeatures("banco");
        });
    }
    
}
