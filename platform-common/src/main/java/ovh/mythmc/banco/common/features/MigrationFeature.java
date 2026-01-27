package ovh.mythmc.banco.common.features;

import java.util.List;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import lombok.RequiredArgsConstructor;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.scheduler.BancoScheduler;
import ovh.mythmc.banco.common.hooks.migration.Migrator;
import ovh.mythmc.banco.common.hooks.migration.TNEMigrator;
import ovh.mythmc.gestalt.Gestalt;
import ovh.mythmc.gestalt.annotations.Feature;
import ovh.mythmc.gestalt.annotations.conditions.FeatureConditionBoolean;
import ovh.mythmc.gestalt.annotations.status.FeatureEnable;

@RequiredArgsConstructor
@Feature(group = "banco", identifier = "MIGRATION")
public final class MigrationFeature {

    private static Migrator MIGRATOR;

    public static @NotNull Optional<Migrator> migrator() {
        return Optional.ofNullable(MIGRATOR);
    }

    public static void clearMigrator() {
        MIGRATOR = null;
    }

    private static final List<Migrator> migrators = List.of(
        new TNEMigrator()
    );

    @FeatureConditionBoolean
    public static boolean canEnable() {
        return Banco.get().getSettings().get().getMigration().isEnabled();
    }

    @FeatureEnable
    public void enable() {
        Gestalt.get().disableFeature(VaultFeature.class);
        
        for (Migrator migrator : migrators) {
            if (Bukkit.getPluginManager().getPlugin(migrator.pluginName()) == null) {
                continue;
            }

            MIGRATOR = migrator;
            break;
        } 

        // Schedule so that it executes once the server is running
        BancoScheduler.get().run(() -> {
            Banco.get().getLogger().warn("The migration feature is active. Most of the plugin's functions will be disabled until the process is complete.");
            Banco.get().getLogger().warn("Use command '/bancomigrate' to start the process.");
        });
    }
    
}
