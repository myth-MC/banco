package ovh.mythmc.banco.common.commands.subcommands;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.BiConsumer;

import net.kyori.adventure.audience.Audience;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.common.util.MessageUtil;

public class DumpSubcommand implements BiConsumer<Audience, String[]> {

    @Override
    public void accept(Audience sender, String[] args) {
        final var dateFormat = new SimpleDateFormat("MMddyyyyHHmm");
        final var dateAsString = dateFormat.format(new Date());
        Banco.get().getAccountManager().getDatabase().backup(dateAsString);

        MessageUtil.debug(sender, "Database has been dumped into accounts.db." + dateAsString + "!");
    }
    
}
