package ovh.mythmc.banco.commands.banco;

import org.bukkit.command.CommandSender;
import ovh.mythmc.banco.Banco;
import ovh.mythmc.banco.utils.MessageUtils;

import java.io.IOException;
import java.util.function.BiConsumer;

public class SaveSubcommand implements BiConsumer<CommandSender, String[]> {

    @Override
    public void accept(CommandSender sender, String[] args) {
        MessageUtils.info(sender, "banco.commands.banco.save");
        try {
            Banco.get().saveData();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        MessageUtils.success(sender, "banco.commands.banco.save.success");
    }

}