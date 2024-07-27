package ovh.mythmc.banco.bukkit.commands.banco;

import org.bukkit.command.CommandSender;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.common.util.MessageUtil;
import ovh.mythmc.banco.bukkit.BancoBukkit;

import java.util.function.BiConsumer;

public class ReloadSubcommand implements BiConsumer<CommandSender, String[]> {

    @Override
    public void accept(CommandSender sender, String[] args) {
        MessageUtil.info(BancoBukkit.adventure().sender(sender), "banco.commands.banco.reload");
        Banco.get().reload();
        MessageUtil.success(BancoBukkit.adventure().sender(sender), "banco.commands.banco.reload.success");
    }

}