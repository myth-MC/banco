package ovh.mythmc.banco.common.features;

import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.common.update.UpdateChecker;
import ovh.mythmc.gestalt.annotations.Feature;
import ovh.mythmc.gestalt.annotations.conditions.FeatureConditionBoolean;
import ovh.mythmc.gestalt.annotations.status.FeatureEnable;

@Feature(group = "banco", identifier = "UPDATE_CHECKER")
public final class UpdateCheckerFeature {

    @FeatureConditionBoolean
    public boolean canBeEnabled() {
        return Banco.get().getSettings().get().getUpdateTracker().isEnabled();
    }

    @FeatureEnable
    public void enable() {
        if (!UpdateChecker.isRunning())
            UpdateChecker.startTask();
    }
    
}
