package ovh.mythmc.banco.common.features;

import org.bukkit.plugin.java.JavaPlugin;

import lombok.RequiredArgsConstructor;
import ovh.mythmc.banco.common.translation.BancoLocalization;
import ovh.mythmc.gestalt.annotations.Feature;
import ovh.mythmc.gestalt.annotations.status.FeatureEnable;
import ovh.mythmc.gestalt.annotations.status.FeatureInitialize;

@Feature(group = "banco", identifier = "LOCALIZATION")
@RequiredArgsConstructor
public final class LocalizationFeature {

    private final JavaPlugin plugin;

    private BancoLocalization localization;

    private boolean enabled = false;

    @FeatureInitialize
    public void initialize() {
        localization = new BancoLocalization(plugin.getDataFolder());
    }

    @FeatureEnable
    public void enable() {
        if (enabled)
            return;

        localization.load();
        enabled = true;
    }
    
}
