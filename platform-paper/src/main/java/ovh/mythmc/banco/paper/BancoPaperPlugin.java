package ovh.mythmc.banco.paper;

import org.bukkit.plugin.java.JavaPlugin;

public class BancoPaperPlugin extends JavaPlugin {

    private BancoPaper bootstrap;

    @Override
    public void onLoad() {
        bootstrap = new BancoPaper(this);
        bootstrap.load();
    }

    @Override
    public void onEnable() {
        bootstrap.initialize();
    }

    @Override
    public void onDisable() {
        bootstrap.shutdown();
    }

}
