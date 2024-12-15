package ovh.mythmc.banco.common.features;

import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.gestalt.annotations.Feature;
import ovh.mythmc.gestalt.annotations.status.FeatureDisable;
import ovh.mythmc.gestalt.annotations.status.FeatureEnable;

@Feature(group = "banco", identifier = "ITEMS")
public final class ItemFeatures {

    @FeatureEnable
    public void enable() {
        // Legacy items (pre 1.0)
        if (Banco.get().getSettings().get().getCurrency().getItems() != null) {
            Banco.get().getLogger().warn("This server's settings.yml contains items configured using the legacy format. It is recommended to migrate to the newer format which includes many new features and options. Please, take a look at https://docs.mythmc.ovh/banco or join our Discord (https://discord.gg/bpkwdzREcR) if you need further assistance");
            Banco.get().getSettings().get().getCurrency().getItems().forEach(bancoItem -> Banco.get().getItemRegistry().register(bancoItem));
        }
        
        // Modern items (post 1.0)
        if (Banco.get().getSettings().get().getCurrency().getItems() == null || Banco.get().getSettings().get().getCurrency().getItems().size() == 0)
            Banco.get().getSettings().get().getCurrency().getItemRegistry().forEach(bancoItem -> {
                Banco.get().getItemRegistry().register(bancoItem);
            });
    }

    @FeatureDisable
    public void disable() {
        Banco.get().getItemRegistry().clear();
    } 
    
}
