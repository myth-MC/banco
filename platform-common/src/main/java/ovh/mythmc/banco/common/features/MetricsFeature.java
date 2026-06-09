package ovh.mythmc.banco.common.features;

import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.RequiredArgsConstructor;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.gestalt.annotations.Feature;
import ovh.mythmc.gestalt.annotations.status.FeatureDisable;
import ovh.mythmc.gestalt.annotations.status.FeatureEnable;

@RequiredArgsConstructor
@Feature(group = "banco", identifier = "METRICS")
public final class MetricsFeature {

    private final JavaPlugin plugin;

    private Metrics metrics = null;

    @FeatureEnable
    public void enable() {
        metrics = new Metrics(plugin, 23496);

        metrics.addCustomChart(new SimplePie("items_mode", () -> {
            if (Banco.get().getItemRegistry().isLegacy())
                return "Legacy";

            return "Modern";
        }));
    }

    @FeatureDisable
    public void disable() {
        metrics.shutdown();
        metrics = null;
    }
    
}
