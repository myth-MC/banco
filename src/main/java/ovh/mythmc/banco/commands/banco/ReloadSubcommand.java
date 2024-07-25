package ovh.mythmc.banco.commands.banco;

import org.bukkit.command.CommandSender;
import ovh.mythmc.banco.Banco;
import ovh.mythmc.banco.utils.MessageUtils;

import java.util.function.BiConsumer;

public class ReloadSubcommand implements BiConsumer<CommandSender, String[]> {

    @Override
    public void accept(CommandSender sender, String[] args) {
        MessageUtils.info(sender, "banco.commands.banco.reload");
        Banco.get().reload();
        MessageUtils.success(sender, "banco.commands.banco.reload.success");
    }

}