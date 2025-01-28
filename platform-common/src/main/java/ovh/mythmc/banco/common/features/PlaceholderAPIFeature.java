package ovh.mythmc.banco.common.features;

import org.bukkit.Bukkit;

import ovh.mythmc.banco.common.hooks.BancoPlaceholderExpansion;
import ovh.mythmc.gestalt.annotations.Feature;
import ovh.mythmc.gestalt.annotations.conditions.FeatureConditionBoolean;
import ovh.mythmc.gestalt.annotations.status.FeatureDisable;
import ovh.mythmc.gestalt.annotations.status.FeatureEnable;

@Feature(group = "banco", identifier = "PLACEHOLDERAPI")
public class PlaceholderAPIFeature {

    private BancoPlaceholderExpansion expansion;

    @FeatureConditionBoolean
    public boolean canBeEnabled() {
        return Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
    }

    @FeatureEnable
    public void enable() {
        if (expansion == null)
            expansion = new BancoPlaceholderExpansion();

        expansion.register();
    }

    @FeatureDisable
    public void disable() {
        expansion.unregister();
    }

}