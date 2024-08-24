package ovh.mythmc.banco.common.commands.subcommands;

import net.kyori.adventure.audience.Audience;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.common.util.MessageUtil;

import java.util.function.BiConsumer;

public class SaveSubcommand implements BiConsumer<Audience, String[]> {

    @Override
    public void accept(Audience sender, String[] args) {
        MessageUtil.info(sender, "banco.commands.banco.save");
        Banco.get().getData().save();
        MessageUtil.success(sender, "banco.commands.banco.save.success");
    }

}