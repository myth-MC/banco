package ovh.mythmc.banco.folia;

import org.bukkit.plugin.java.JavaPlugin;

public class BancoFoliaPlugin extends JavaPlugin {

    private BancoFolia bootstrap;

    @Override
    public void onEnable() {
        bootstrap = new BancoFolia(this);
        bootstrap.initialize();
    }

    @Override
    public void onDisable() {
        bootstrap.shutdown();
    }

}
