package ovh.mythmc.banco.bukkit;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.audience.Audience;
import ovh.mythmc.banco.common.command.sender.BancoCommandSource;

public final class BukkitCommandSource implements BancoCommandSource {

    private final CommandSender source;

    public BukkitCommandSource(@NotNull CommandSender source) {
        this.source = source;
    }

    @Override
    public @NotNull Audience audience() {
        return BancoBukkit.adventure().sender((@NotNull CommandSender) this.source);
    }

    @Override
    public CommandSender source() {
        return this.source;
    }

    @Override
    public boolean isPlayer() {
        return this.source() instanceof Player;
    }

    @Override
    public String name() {
        return this.source().getName();
    }
    
}
