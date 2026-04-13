package ovh.mythmc.banco.common.features;

import org.bukkit.plugin.java.JavaPlugin;

import lombok.RequiredArgsConstructor;
import ovh.mythmc.banco.common.hooks.BancoEconomyHook;
import ovh.mythmc.banco.common.hooks.BancoVaultHook;
import ovh.mythmc.banco.common.hooks.BancoVaultUnlockedHook;
import ovh.mythmc.gestalt.Gestalt;
import ovh.mythmc.gestalt.annotations.Feature;
import ovh.mythmc.gestalt.annotations.conditions.FeatureConditionBoolean;
import ovh.mythmc.gestalt.annotations.status.FeatureDisable;
import ovh.mythmc.gestalt.annotations.status.FeatureEnable;
import ovh.mythmc.gestalt.annotations.status.FeatureInitialize;

@RequiredArgsConstructor
@Feature(group = "banco", identifier = "VAULT")
public final class VaultFeature {

    private final JavaPlugin plugin;

    private BancoEconomyHook economyHook;

    @FeatureInitialize
    public void initialize() {
        economyHook = isVaultUnlocked() ? new BancoVaultUnlockedHook() : new BancoVaultHook();
    }

    @FeatureConditionBoolean
    public boolean canEnable() {
        return !Gestalt.get().isEnabled(MigrationFeature.class);
    }
    
    @FeatureEnable
    public void enable() {
        economyHook.hook(plugin);
    }

    @FeatureDisable
    public void disable() {
        economyHook.unhook();
    }

    private static boolean isVaultUnlocked() {
        try {
            Class.forName("net.milkbowl.vault2.economy.Economy");
            return true;
        } catch (ClassNotFoundException ignored) {
        }

        return false;
    }

}
