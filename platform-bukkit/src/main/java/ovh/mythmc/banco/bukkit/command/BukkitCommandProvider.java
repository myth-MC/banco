package ovh.mythmc.banco.bukkit.command;

import org.incendo.cloud.paper.LegacyPaperCommandManager;

import ovh.mythmc.banco.bukkit.BukkitCommandSource;
import ovh.mythmc.banco.common.command.BancoCommandProvider;

public final class BukkitCommandProvider extends BancoCommandProvider<BukkitCommandSource> {

    public BukkitCommandProvider(LegacyPaperCommandManager<BukkitCommandSource> commandManager) {
        super(commandManager);
    }
    
}
