package ovh.mythmc.banco.common.hooks;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.accounts.Account;
import ovh.mythmc.banco.common.util.MessageUtil;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

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
        return Banco.get().version();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        Account account = Banco.get().getAccountManager().get(player.getUniqueId());
        if (account == null)
            return null;

        if (params.equalsIgnoreCase("balance")) {
            return MessageUtil.format(account.amount());
        } else if (params.equalsIgnoreCase("symbol")) {
            return Banco.get().getSettings().get().getCurrency().getSymbol();
        } else if (params.equalsIgnoreCase("name_plural")) {
            return Banco.get().getSettings().get().getCurrency().getNamePlural();
        } else if (params.equalsIgnoreCase("name_singular")) {
            return Banco.get().getSettings().get().getCurrency().getNameSingular();
        } else if (params.equalsIgnoreCase("version")) {
            return Banco.get().version();
        } else if (params.startsWith("top_")) {
            int pos = Integer.parseInt(params.substring(4));
            Map.Entry<UUID, BigDecimal> entry = Banco.get().getAccountManager().getTopPosition(pos);
            if (entry == null)
                return null;
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(entry.getKey());
            return offlinePlayer.getName();
        }

        return null;
    }

}
