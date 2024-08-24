package ovh.mythmc.banco.paper.commands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.banco.common.commands.PayCommand;

import java.util.Collection;

@SuppressWarnings("UnstableApiUsage")
public class PayCommandImpl extends PayCommand implements BasicCommand {

    @Override
    public void execute(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        run(stack.getSender(), args);
    }

    @Override
    public @NotNull Collection<String> suggest(@NotNull CommandSourceStack commandSourceStack, @NotNull String[] args) {
        return getSuggestions(args);
    }

    @Override
    public String permission() {
        return "banco.user";
    }

}
