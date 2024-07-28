package ovh.mythmc.banco.common;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.banco.api.Banco;

public class BancoPlaceholderExpansion extends PlaceholderExpansion {

    public BancoPlaceholderExpansion() {
        register();
    }

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
        return Banco.get().version();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (params.equalsIgnoreCase("balance")) {
            return Integer.toString(Banco.get().getInventoryValue(player.getUniqueId()));
        }

        return null;
    }

}
