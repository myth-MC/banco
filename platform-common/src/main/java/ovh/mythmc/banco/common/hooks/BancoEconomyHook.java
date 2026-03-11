package ovh.mythmc.banco.common.hooks;

import org.bukkit.plugin.Plugin;

public interface BancoEconomyHook {

    void hook(Plugin plugin);

    void unhook();
    
}
