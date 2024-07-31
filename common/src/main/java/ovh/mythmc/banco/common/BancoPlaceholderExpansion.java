package ovh.mythmc.banco.common;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.economy.BancoHelper;
import ovh.mythmc.banco.common.util.MessageUtil;

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
            return MessageUtil.format(BancoHelper.get().getInventoryValue(player.getUniqueId()));
        } else if (params.equalsIgnoreCase("symbol")) {
            return Banco.get().getConfig().getSettings().getCurrency().symbol();
        } else if (params.equalsIgnoreCase("name_plural")) {
            return Banco.get().getConfig().getSettings().getCurrency().namePlural();
        } else if (params.equalsIgnoreCase("name_singular")) {
            return Banco.get().getConfig().getSettings().getCurrency().nameSingular();
        } else if (params.equalsIgnoreCase("version")) {
            return Banco.get().version();
        }

        return null;
    }

}
