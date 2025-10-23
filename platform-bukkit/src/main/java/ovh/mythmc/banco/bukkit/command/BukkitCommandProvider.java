package ovh.mythmc.banco.bukkit.command;

import org.incendo.cloud.paper.LegacyPaperCommandManager;

import ovh.mythmc.banco.common.command.BancoCommandProvider;
import ovh.mythmc.banco.common.command.sender.BancoCommandSource;

public final class BukkitCommandProvider extends BancoCommandProvider {

    public BukkitCommandProvider(LegacyPaperCommandManager<BancoCommandSource> commandManager) {
        super(commandManager);
    }
    
}
