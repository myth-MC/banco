package ovh.mythmc.banco.bukkit;

import org.bukkit.plugin.java.JavaPlugin;

public class BancoBukkitPlugin extends JavaPlugin {

    private BancoBukkit bootstrap;

    @Override
    public void onLoad() {
        bootstrap = new BancoBukkit(this);
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
