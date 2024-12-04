package ovh.mythmc.banco.paper;

import org.bukkit.plugin.java.JavaPlugin;

public class BancoPaperPlugin extends JavaPlugin {

    private BancoPaper bootstrap;

    @Override
    public void onEnable() {
        bootstrap = new BancoPaper(this);
        bootstrap.initialize();
    }

    @Override
    public void onDisable() {
        bootstrap.disable();
        bootstrap.shutdown();
    }

}
