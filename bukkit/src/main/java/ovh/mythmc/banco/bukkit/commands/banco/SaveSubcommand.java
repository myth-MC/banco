package ovh.mythmc.banco.bukkit.commands.banco;

import org.bukkit.command.CommandSender;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.bukkit.BancoBukkit;
import ovh.mythmc.banco.common.util.MessageUtil;

import java.io.IOException;
import java.util.function.BiConsumer;

public class SaveSubcommand implements BiConsumer<CommandSender, String[]> {

    @Override
    public void accept(CommandSender sender, String[] args) {
        MessageUtil.info(BancoBukkit.adventure().sender(sender), "banco.commands.banco.save");
        try {
            Banco.get().getStorage().saveData();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        MessageUtil.success(BancoBukkit.adventure().sender(sender), "banco.commands.banco.save.success");
    }

}