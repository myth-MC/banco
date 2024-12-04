package ovh.mythmc.banco.bukkit;

import org.bukkit.plugin.java.JavaPlugin;

public class BancoBukkitPlugin extends JavaPlugin {

    private BancoBukkit bootstrap;

    @Override
    public void onEnable() {
        bootstrap = new BancoBukkit(this);
        bootstrap.initialize();
    }

    @Override
    public void onDisable() {
        bootstrap.disable();
        bootstrap.shutdown();
    }

}
