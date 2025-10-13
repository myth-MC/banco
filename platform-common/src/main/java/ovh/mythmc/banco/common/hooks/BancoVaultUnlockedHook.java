package ovh.mythmc.banco.common.hooks;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;

import net.milkbowl.vault2.economy.AccountPermission;
import net.milkbowl.vault2.economy.EconomyResponse;
import net.milkbowl.vault2.economy.EconomyResponse.ResponseType;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.accounts.AccountIdentifierKey;
import ovh.mythmc.banco.common.util.MessageUtil;

public final class BancoVaultUnlockedHook implements net.milkbowl.vault2.economy.Economy {

    private boolean enabled = false;

    public BancoVaultUnlockedHook() {
        this.enabled = true;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public @NotNull String getName() {
        return "banco";
    }

    @Override
    public boolean hasSharedAccountSupport() {
        return false;
    }

    @Override
    public boolean hasMultiCurrencySupport() {
        return false;
    }

    @Override
    public @NotNull int fractionalDigits(@NotNull String pluginName) {
        DecimalFormat format = new DecimalFormat(Banco.get().getSettings().get().getCurrency().getFormat());
        return format.getMaximumFractionDigits();
    }

    @Override
    public @NotNull String format(@NotNull BigDecimal amount) {
        return format("banco", amount);
    }

    @Override
    public @NotNull String format(@NotNull String pluginName, @NotNull BigDecimal amount) {
        return MessageUtil.format(amount) + Banco.get().getSettings().get().getCurrency().getSymbol();
    }

    @Override
    public @NotNull String format(@NotNull BigDecimal amount, @NotNull String currency) {
        return format("banco", amount, currency);
    }

    @Override
    public @NotNull String format(@NotNull String pluginName, @NotNull BigDecimal amount, @NotNull String currency) {
        return MessageUtil.format(amount) + Banco.get().getSettings().get().getCurrency().getSymbol();
    }

    @Override
    public boolean hasCurrency(@NotNull String currency) {
        return Banco.get().getSettings().get().getCurrency().getNameSingular().equalsIgnoreCase(currency);
    }

    @Override
    public @NotNull String getDefaultCurrency(@NotNull String pluginName) {
       return Banco.get().getSettings().get().getCurrency().getNameSingular();
    }

    @Override
    public @NotNull String defaultCurrencyNamePlural(@NotNull String pluginName) {
        return Banco.get().getSettings().get().getCurrency().getNamePlural();
    }

    @Override
    public @NotNull String defaultCurrencyNameSingular(@NotNull String pluginName) {
        return Banco.get().getSettings().get().getCurrency().getNameSingular();
    }

    @Override
    public @NotNull Collection<String> currencies() {
        return List.of(Banco.get().getSettings().get().getCurrency().getNameSingular());
    }

    @Override
    public boolean createAccount(@NotNull UUID accountID, @NotNull String name) {
        Banco.get().getAccountManager().create(accountID, name);
        return true;
    }

    @Override
    public boolean createAccount(@NotNull UUID accountID, @NotNull String name, boolean player) {
        return createAccount(accountID, name);
    }

    @Override
    public boolean createAccount(@NotNull UUID accountID, @NotNull String name, @NotNull String worldName) {
        return createAccount(accountID, name);
    }

    @Override
    public boolean createAccount(@NotNull UUID accountID, @NotNull String name, @NotNull String worldName,
            boolean player) {
        return createAccount(accountID, name);
    }

    @Override
    public @NotNull Map<UUID, String> getUUIDNameMap() {
        return Banco.get().getAccountManager().getDatabase().getAccountIdentifierCache().stream()
            .collect(Collectors.toMap(AccountIdentifierKey::uuid, AccountIdentifierKey::name));
    }

    @Override
    public Optional<String> getAccountName(@NotNull UUID accountID) {
        return Optional.ofNullable(Banco.get().getAccountManager().getByUuid(accountID).getIdentifier().name());
    }

    @Override
    public boolean hasAccount(@NotNull UUID accountID) {
        return Banco.get().getAccountManager().getByUuid(accountID) != null;
    }

    @Override
    public boolean hasAccount(@NotNull UUID accountID, @NotNull String worldName) {
        return hasAccount(accountID);
    }

    @Override
    public boolean renameAccount(@NotNull UUID accountID, @NotNull String name) {
        return false;
    }

    @Override
    public boolean renameAccount(@NotNull String plugin, @NotNull UUID accountID, @NotNull String name) {
        return false;
    }

    @Override
    public boolean deleteAccount(@NotNull String plugin, @NotNull UUID accountID) {
        Banco.get().getAccountManager().delete(accountID);
        return true;
    }

    @Override
    public boolean accountSupportsCurrency(@NotNull String plugin, @NotNull UUID accountID, @NotNull String currency) {
        return hasAccount(accountID) && Banco.get().getSettings().get().getCurrency().getNameSingular().equalsIgnoreCase(currency);
    }

    @Override
    public boolean accountSupportsCurrency(@NotNull String plugin, @NotNull UUID accountID, @NotNull String currency,
            @NotNull String world) {
        return accountSupportsCurrency(plugin, accountID, currency);
    }

    @Override
    public @NotNull BigDecimal getBalance(@NotNull String pluginName, @NotNull UUID accountID) {
        return Banco.get().getAccountManager().getByUuid(accountID).amount();
    }

    @Override
    public @NotNull BigDecimal getBalance(@NotNull String pluginName, @NotNull UUID accountID, @NotNull String world) {
        return getBalance(pluginName, accountID);
    }

    @Override
    public @NotNull BigDecimal getBalance(@NotNull String pluginName, @NotNull UUID accountID, @NotNull String world,
            @NotNull String currency) {
        return getBalance(pluginName, accountID);
    }

    @Override
    public boolean has(@NotNull String pluginName, @NotNull UUID accountID, @NotNull BigDecimal amount) {
        return Banco.get().getAccountManager().has(accountID, amount);
    }

    @Override
    public boolean has(@NotNull String pluginName, @NotNull UUID accountID, @NotNull String worldName,
            @NotNull BigDecimal amount) {
        return has(pluginName, accountID, amount);
    }

    @Override
    public boolean has(@NotNull String pluginName, @NotNull UUID accountID, @NotNull String worldName,
            @NotNull String currency, @NotNull BigDecimal amount) {
        return has(pluginName, accountID, amount);
    }

    @Override
    public @NotNull EconomyResponse withdraw(@NotNull String pluginName, @NotNull UUID accountID,
            @NotNull BigDecimal amount) {

        final var account = Banco.get().getAccountManager().getByUuid(accountID);
        if (!has(pluginName, accountID, amount))
            return new EconomyResponse(amount, account.amount(), ResponseType.FAILURE, "Not enough funds");

        Banco.get().getAccountManager().withdraw(accountID, amount);
        return new EconomyResponse(amount, account.amount(), ResponseType.SUCCESS, "");
    }

    @Override
    public @NotNull EconomyResponse withdraw(@NotNull String pluginName, @NotNull UUID accountID,
            @NotNull String worldName, @NotNull BigDecimal amount) {
        return withdraw(pluginName, accountID, amount);
    }

    @Override
    public @NotNull EconomyResponse withdraw(@NotNull String pluginName, @NotNull UUID accountID,
            @NotNull String worldName, @NotNull String currency, @NotNull BigDecimal amount) {
        return withdraw(pluginName, accountID, amount);
    }

    @Override
    public @NotNull EconomyResponse deposit(@NotNull String pluginName, @NotNull UUID accountID,
            @NotNull BigDecimal amount) {

        final var account = Banco.get().getAccountManager().getByUuid(accountID);

        Banco.get().getAccountManager().deposit(accountID, amount);
        return new EconomyResponse(amount, account.amount(), ResponseType.SUCCESS, "");
    }

    @Override
    public @NotNull EconomyResponse deposit(@NotNull String pluginName, @NotNull UUID accountID,
            @NotNull String worldName, @NotNull BigDecimal amount) {
        return deposit(pluginName, accountID, amount);
    }

    @Override
    public @NotNull EconomyResponse deposit(@NotNull String pluginName, @NotNull UUID accountID,
            @NotNull String worldName, @NotNull String currency, @NotNull BigDecimal amount) {
        return deposit(pluginName, accountID, amount);
    }

    @Override
    public boolean createSharedAccount(@NotNull String pluginName, @NotNull UUID accountID, @NotNull String name,
            @NotNull UUID owner) {
        return false;
    }

    @Override
    public boolean isAccountOwner(@NotNull String pluginName, @NotNull UUID accountID, @NotNull UUID uuid) {
        return accountID.equals(uuid);
    }

    @Override
    public boolean setOwner(@NotNull String pluginName, @NotNull UUID accountID, @NotNull UUID uuid) {
        return false;
    }

    @Override
    public boolean isAccountMember(@NotNull String pluginName, @NotNull UUID accountID, @NotNull UUID uuid) {
        return accountID.equals(uuid);
    }

    @Override
    public boolean addAccountMember(@NotNull String pluginName, @NotNull UUID accountID, @NotNull UUID uuid) {
        return false;
    }

    @Override
    public boolean addAccountMember(@NotNull String pluginName, @NotNull UUID accountID, @NotNull UUID uuid,
            @NotNull AccountPermission... initialPermissions) {
                return false;
    }

    @Override
    public boolean removeAccountMember(@NotNull String pluginName, @NotNull UUID accountID, @NotNull UUID uuid) {
        return false;
    }

    @Override
    public boolean hasAccountPermission(@NotNull String pluginName, @NotNull UUID accountID, @NotNull UUID uuid,
            @NotNull AccountPermission permission) {
        return false;
    }

    @Override
    public boolean updateAccountPermission(@NotNull String pluginName, @NotNull UUID accountID, @NotNull UUID uuid,
            @NotNull AccountPermission permission, boolean value) {
        return false;
    }
    
}
