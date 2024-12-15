package ovh.mythmc.banco.common.features;

import org.bukkit.plugin.java.JavaPlugin;

import lombok.RequiredArgsConstructor;
import ovh.mythmc.banco.common.hooks.BancoVaultHook;
import ovh.mythmc.gestalt.annotations.Feature;
import ovh.mythmc.gestalt.annotations.status.FeatureDisable;
import ovh.mythmc.gestalt.annotations.status.FeatureEnable;
import ovh.mythmc.gestalt.annotations.status.FeatureInitialize;

@RequiredArgsConstructor
@Feature(group = "banco", identifier = "VAULT")
public final class VaultFeature {

    private final JavaPlugin plugin;

    private BancoVaultHook vaultImpl;

    @FeatureInitialize
    public void initialize() {
        vaultImpl = new BancoVaultHook();
    }
    
    @FeatureEnable
    public void enable() {
        vaultImpl.hook(plugin);
    }

    @FeatureDisable
    public void disable() {
        vaultImpl.unhook();
    }

}
