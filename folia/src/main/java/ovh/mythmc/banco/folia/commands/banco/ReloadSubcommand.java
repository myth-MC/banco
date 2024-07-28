package ovh.mythmc.banco.folia.commands.banco;

import org.bukkit.command.CommandSender;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.common.util.MessageUtil;

import java.util.function.BiConsumer;

public class ReloadSubcommand implements BiConsumer<CommandSender, String[]> {

    @Override
    public void accept(CommandSender sender, String[] args) {
        MessageUtil.info(sender, "banco.commands.banco.reload");
        Banco.get().reload();
        MessageUtil.success(sender, "banco.commands.banco.reload.success");
    }

}