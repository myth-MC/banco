package ovh.mythmc.banco.common.listeners;

import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.common.hooks.BancoSocialHook;
import ovh.mythmc.gestalt.annotations.FeatureListener;
import ovh.mythmc.gestalt.features.FeatureEvent;

public final class GestaltListener {

    private BancoSocialHook hook;

    @FeatureListener(group = "social", identifier = "REACTIONS", events = { FeatureEvent.ENABLE })
    public void onSocialReactionsEnable() {
        Banco.get().getLogger().info("listener GestaltListener funciona (reaccion)");
        hook = new BancoSocialHook();
        hook.registerKeyword();
        hook.registerReaction();
    }    

}
