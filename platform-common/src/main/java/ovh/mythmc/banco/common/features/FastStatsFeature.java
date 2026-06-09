package ovh.mythmc.banco.common.features;

import org.bukkit.plugin.java.JavaPlugin;

import dev.faststats.ErrorTracker;
import dev.faststats.Metrics;
import dev.faststats.bukkit.BukkitContext;
import ovh.mythmc.banco.api.scheduler.BancoScheduler;
import ovh.mythmc.gestalt.annotations.Feature;
import ovh.mythmc.gestalt.annotations.status.FeatureDisable;
import ovh.mythmc.gestalt.annotations.status.FeatureEnable;

@Feature(group = "banco", identifier = "FAST_STATS")
public final class FastStatsFeature {

    public static final ErrorTracker ERROR_TRACKER = ErrorTracker.contextAware();

    private final BukkitContext context;

    public FastStatsFeature(JavaPlugin plugin) {
        this.context = new BukkitContext.Factory(plugin, "d65d381513288da05a6032eb58ec9c98")
            .metrics(Metrics.Factory::create)
            .errorTrackerService(ERROR_TRACKER)
            .create();
    }
    @FeatureEnable
    public void enable() {
        BancoScheduler.get().run(() -> {
            context.ready();
        });
    }

    @FeatureDisable
    public void disable() {
        context.shutdown();
    }
    
}
