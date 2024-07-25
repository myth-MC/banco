package ovh.mythmc.banco;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class BancoPlaceholderExpansion extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "banco";
    }

    @Override
    public @NotNull String getAuthor() {
        return "U8092";
    }

    @Override
    public @NotNull String getVersion() {
        return Banco.get().getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (params.equalsIgnoreCase("balance")) {
            return Integer.toString(Banco.get().getAccountManager().convertItemsToAmount(player.getPlayer().getInventory().getContents()));
        }

        return null;
    }
}
