package ovh.mythmc.banco;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import ovh.mythmc.banco.utils.PlayerUtils;

import java.util.Collections;
import java.util.List;

public class BancoVaultImpl implements Economy {

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return "banco";
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return 0;
    }

    @Override
    public String format(double v) {
        return v + Banco.getInstance().getConfig().getString("currency.symbol");
    }

    @Override
    public String currencyNamePlural() {
        return Banco.getInstance().getConfig().getString("currency.name.plural");
    }

    @Override
    public String currencyNameSingular() {
        return Banco.getInstance().getConfig().getString("currency.name.singular");
    }

    @Override
    public boolean hasAccount(String s) {
        return Banco.getInstance().getEconomyManager().getAccount(PlayerUtils.getUuid(s)) != null;
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer) {
        return hasAccount(offlinePlayer.getName());
    }

    @Override
    public boolean hasAccount(String s, String s1) {
        return hasAccount(s);
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer, String s) {
        return hasAccount(offlinePlayer.getName());
    }

    @Override
    public double getBalance(String s) {
        if (!hasAccount(s))
            return 0;

        return Banco.getInstance().getEconomyManager().getActualAmount(Bukkit.getOfflinePlayer(PlayerUtils.getUuid(s)));
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer) {
        return getBalance(offlinePlayer.getName());
    }

    @Override
    public double getBalance(String s, String s1) {
        return getBalance(s);
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer, String s) {
        return getBalance(offlinePlayer);
    }

    @Override
    public boolean has(String s, double v) {
        return getBalance(s) >= v;
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, double v) {
        return has(offlinePlayer.getName(), v);
    }

    @Override
    public boolean has(String s, String s1, double v) {
        return has(s, v);
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, String s, double v) {
        return has(offlinePlayer.getName(), v);
    }

    @Override
    public EconomyResponse withdrawPlayer(String s, double v) {
        if (!hasAccount(s)) {
            Banco.getInstance().getEconomyManager().createAccount(PlayerUtils.getUuid(s));
        }

        Banco.getInstance().getEconomyManager().remove(Bukkit.getOfflinePlayer(PlayerUtils.getUuid(s)), (int) v);

        return new EconomyResponse(v,
                Banco.getInstance().getEconomyManager().getActualAmount(Bukkit.getOfflinePlayer(PlayerUtils.getUuid(s))),
                EconomyResponse.ResponseType.SUCCESS,
                "");
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, double v) {
        return withdrawPlayer(offlinePlayer.getName(), v);
    }

    @Override
    public EconomyResponse withdrawPlayer(String s, String s1, double v) {
        return withdrawPlayer(s, v);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, String s, double v) {
        return withdrawPlayer(offlinePlayer.getName(), v);
    }

    @Override
    public EconomyResponse depositPlayer(String s, double v) {
        if (!hasAccount(s)) {
            Banco.getInstance().getEconomyManager().createAccount(Bukkit.getOfflinePlayer(PlayerUtils.getUuid(s)).getUniqueId());
        }

        Banco.getInstance().getEconomyManager().add(Bukkit.getOfflinePlayer(PlayerUtils.getUuid(s)), (int) v);

        return new EconomyResponse(v,
                Banco.getInstance().getEconomyManager().getActualAmount(Bukkit.getOfflinePlayer(PlayerUtils.getUuid(s))),
                EconomyResponse.ResponseType.SUCCESS,
                "");
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, double v) {
        return depositPlayer(offlinePlayer.getName(), v);
    }

    @Override
    public EconomyResponse depositPlayer(String s, String s1, double v) {
        return depositPlayer(s, v);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, String s, double v) {
        return depositPlayer(offlinePlayer.getName(), v);
    }

    @Override
    public EconomyResponse createBank(String s, String s1) {
        return new EconomyResponse(0L,
                0L,
                EconomyResponse.ResponseType.NOT_IMPLEMENTED,
                "Banks are not implemented in " + getName());
    }

    @Override
    public EconomyResponse createBank(String s, OfflinePlayer offlinePlayer) {
        return new EconomyResponse(0L,
                0L,
                EconomyResponse.ResponseType.NOT_IMPLEMENTED,
                "Banks are not implemented in " + getName());
    }

    @Override
    public EconomyResponse deleteBank(String s) {
        return new EconomyResponse(0L,
                0L,
                EconomyResponse.ResponseType.NOT_IMPLEMENTED,
                "Banks are not implemented in " + getName());
    }

    @Override
    public EconomyResponse bankBalance(String s) {
        return new EconomyResponse(0L,
                0L,
                EconomyResponse.ResponseType.NOT_IMPLEMENTED,
                "Banks are not implemented in " + getName());
    }

    @Override
    public EconomyResponse bankHas(String s, double v) {
        return new EconomyResponse(0L,
                0L,
                EconomyResponse.ResponseType.NOT_IMPLEMENTED,
                "Banks are not implemented in " + getName());
    }

    @Override
    public EconomyResponse bankWithdraw(String s, double v) {
        return new EconomyResponse(0L,
                0L,
                EconomyResponse.ResponseType.NOT_IMPLEMENTED,
                "Banks are not implemented in " + getName());
    }

    @Override
    public EconomyResponse bankDeposit(String s, double v) {
        return new EconomyResponse(0L,
                0L,
                EconomyResponse.ResponseType.NOT_IMPLEMENTED,
                "Banks are not implemented in " + getName());
    }

    @Override
    public EconomyResponse isBankOwner(String s, String s1) {
        return new EconomyResponse(0L,
                0L,
                EconomyResponse.ResponseType.NOT_IMPLEMENTED,
                "Banks are not implemented in " + getName());
    }

    @Override
    public EconomyResponse isBankOwner(String s, OfflinePlayer offlinePlayer) {
        return new EconomyResponse(0L,
                0L,
                EconomyResponse.ResponseType.NOT_IMPLEMENTED,
                "Banks are not implemented in " + getName());
    }

    @Override
    public EconomyResponse isBankMember(String s, String s1) {
        return new EconomyResponse(0L,
                0L,
                EconomyResponse.ResponseType.NOT_IMPLEMENTED,
                "Banks are not implemented in " + getName());
    }

    @Override
    public EconomyResponse isBankMember(String s, OfflinePlayer offlinePlayer) {
        return new EconomyResponse(0L,
                0L,
                EconomyResponse.ResponseType.NOT_IMPLEMENTED,
                "Banks are not implemented in " + getName());
    }

    @Override
    public List<String> getBanks() {
        return Collections.emptyList();
    }

    @Override
    public boolean createPlayerAccount(String s) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(String s, String s1) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer, String s) {
        return false;
    }
}
