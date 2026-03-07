package ovh.mythmc.banco.common.features;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import ovh.mythmc.banco.common.listeners.InteractionListener;
import ovh.mythmc.gestalt.annotations.Feature;
import ovh.mythmc.gestalt.annotations.status.FeatureDisable;
import ovh.mythmc.gestalt.annotations.status.FeatureEnable;

@Feature(group = "banco", identifier = "INTERACTION_RESTRICTION")
public final class InteractionRestrictionFeature {
    
    private final JavaPlugin plugin;
    private final InteractionListener listener = new InteractionListener();

    public InteractionRestrictionFeature(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @FeatureEnable
    public void enable() {
        Bukkit.getPluginManager().registerEvents(listener, plugin);
    }

    @FeatureDisable
    public void disable() {
        HandlerList.unregisterAll(listener);
    }

}
