package ovh.mythmc.banco.common.commands.subcommands;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.common.menus.MenuManager;
import ovh.mythmc.banco.common.menus.impl.InfoMenu;
import ovh.mythmc.banco.common.update.UpdateChecker;
import ovh.mythmc.banco.common.util.MessageUtil;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

public class InfoSubcommand implements BiConsumer<Audience, String[]> {

    @Override
    public void accept(Audience sender, String[] args) {
        if (args.length == 0) {
            Optional<UUID> uuid = sender.get(Identity.UUID);
            if (uuid.isEmpty())
                return;

            MenuManager.getInstance().openInventory(new InfoMenu(), Objects.requireNonNull(Bukkit.getPlayer(uuid.get())));
        } else if (args[0].equalsIgnoreCase("dump")) {
            String version = Banco.get().version();
            String latest = UpdateChecker.getLatest();

            MessageUtil.info(sender, translatable("banco.commands.banco", text(version), text(getBancoBuildSoftware())));
            if (!version.equals(latest)) {
                MessageUtil.info(sender, translatable("banco.commands.banco.new-version", text(latest))
                        .clickEvent(ClickEvent.openUrl("https://github.com/myth-MC/banco/releases/tag/" + latest)));
            }

            MessageUtil.debug(sender, translatable("banco.commands.banco.debug.1",
                    text(org.bukkit.Bukkit.getBukkitVersion())
            ));

            MessageUtil.debug(sender, translatable("banco.commands.banco.debug.2",
                    text(Bukkit.getServer().getOnlineMode())
            ));

            MessageUtil.debug(sender, translatable("banco.commands.banco.debug.3",
                    text(Banco.get().getItemManager().get().size()),
                    text(Banco.get().getStorageManager().get().size()),
                    text(Banco.get().getAccountManager().get().size())
            ));
        }
    }

    private String getBancoBuildSoftware() {
        try {
            Class.forName("ovh.mythmc.banco.paper.BancoPaperPlugin");
            return "paper";
        } catch (ClassNotFoundException ignored) {
        }

        return "bukkit";
    }

}