package ovh.mythmc.banco.common.hooks;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.economy.BancoHelper;
import ovh.mythmc.banco.api.logger.LoggerWrapper;
import ovh.mythmc.banco.common.util.MessageUtil;
import ovh.mythmc.banco.common.util.PlayerUtil;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class BancoVaultHook implements Economy {

    private final LoggerWrapper logger = new LoggerWrapper() {
        @Override
        public void info(final String message, final Object... args) {
            Banco.get().getLogger().info("[vault-impl] " + message, args);
        }

        @Override
        public void warn(final String message, final Object... args) {
            Banco.get().getLogger().warn("[vault-impl] " + message, args);
        }

        @Override
        public void error(final String message, final Object... args) {
            Banco.get().getLogger().error("[vault-impl] " + message, args);
        }
    };

    public void hook(Plugin plugin) {
        Bukkit.getServer().getServicesManager().register(net.milkbowl.vault.economy.Economy.class, this, plugin, ServicePriority.Highest);
        logger.info("Hooked with Vault " + Bukkit.getPluginManager().getPlugin("Vault").getDescription().getVersion());
    }

    public void unhook() {
        Bukkit.getServicesManager().unregister(Economy.class, this);
    }

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
        return MessageUtil.format(BigDecimal.valueOf(v)) + Banco.get().getSettings().get().getCurrency().getSymbol();
    }

    @Override
    public String currencyNamePlural() {
        return Banco.get().getSettings().get().getCurrency().getNamePlural();
    }

    @Override
    public String currencyNameSingular() {
        return Banco.get().getSettings().get().getCurrency().getNameSingular();
    }

    @Override
    public boolean hasAccount(String s) {
        return Banco.get().getAccountManager().get(s) != null;
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

        UUID uuid = PlayerUtil.getUuid(s);

        if (BancoHelper.get().isInBlacklistedWorld(uuid))
            return 0;

        return Banco.get().getAccountManager().amount(uuid).doubleValue();
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
        UUID uuid = PlayerUtil.getUuid(s);

        if (!hasAccount(s)) {
            Banco.get().getAccountManager().create(uuid);
        }

        if (BancoHelper.get().isInBlacklistedWorld(uuid))
            return new EconomyResponse(0,
                    getBalance(Bukkit.getOfflinePlayer(uuid)),
                    EconomyResponse.ResponseType.FAILURE,
                    "Player is in blacklisted world");

        Banco.get().getAccountManager().withdraw(uuid, BigDecimal.valueOf(v));

        return new EconomyResponse(v,
                getBalance(Bukkit.getOfflinePlayer(uuid)),
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
        UUID uuid = PlayerUtil.getUuid(s);

        if (!hasAccount(s)) {
            Banco.get().getAccountManager().create(uuid);
        }

        if (BancoHelper.get().isInBlacklistedWorld(uuid))
            return new EconomyResponse(0,
                    getBalance(Bukkit.getOfflinePlayer(uuid)),
                    EconomyResponse.ResponseType.FAILURE,
                    "Player is in blacklisted world");

        Banco.get().getAccountManager().deposit(uuid, BigDecimal.valueOf(v));

        return new EconomyResponse(v,
                getBalance(Bukkit.getOfflinePlayer(uuid)),
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
    public boolean createPlayerAccount(String s) { // FAKE PLAYER ACCOUNT
        if (hasAccount(s))
            return false;

        Banco.get().getAccountManager().create(PlayerUtil.getUuid(s));
        return true;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer) {
        return createPlayerAccount(offlinePlayer.getName());
    }

    @Override
    public boolean createPlayerAccount(String s, String s1) {
        return createPlayerAccount(s);
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer, String s) {
        return createPlayerAccount(offlinePlayer.getName());
    }
}
