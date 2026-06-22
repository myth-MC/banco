package ovh.mythmc.banco.paper;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.audience.Audience;
import ovh.mythmc.banco.common.command.sender.BancoCommandSource;

public final class PaperCommandSource implements BancoCommandSource {

    private final CommandSourceStack commandSourceStack;

    public PaperCommandSource(@NotNull CommandSourceStack commandSourceStack) {
        this.commandSourceStack = commandSourceStack;
    }

    @Override
    public @NotNull Audience audience() {
        return this.commandSourceStack.getSender();
    }

    @Override
    public CommandSender source() {
        return this.commandSourceStack.getSender();
    }

    @Override
    public boolean isPlayer() {
        return this.commandSourceStack.getSender() instanceof Player;
    }

    @Override
    public String name() {
        return this.commandSourceStack.getSender().getName();
    }

    public CommandSourceStack commandSourceStack() {
        return this.commandSourceStack;
    }
    
}
