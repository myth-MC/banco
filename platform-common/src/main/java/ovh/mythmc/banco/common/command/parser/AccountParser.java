package ovh.mythmc.banco.common.command.parser;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.parser.ParserDescriptor;
import org.incendo.cloud.suggestion.BlockingSuggestionProvider;
import org.jetbrains.annotations.NotNull;

import io.leangen.geantyref.TypeToken;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.accounts.Account;
import ovh.mythmc.banco.api.accounts.AccountIdentifierKey;
import ovh.mythmc.banco.common.command.exception.UnknownAccountException;
import ovh.mythmc.banco.common.command.sender.BancoCommandSource;

public final class AccountParser implements ArgumentParser<BancoCommandSource, Account>, BlockingSuggestionProvider.Strings<BancoCommandSource>, ParserDescriptor<BancoCommandSource, Account> {

    private final Mode mode;

    private AccountParser(final @NotNull Mode mode) { 
        this.mode = mode;
    }

    public static AccountParser accountParser() {
        return all();
    }

    public static AccountParser all() {
        return new AccountParser(Mode.ALL);
    }

    public static AccountParser onlinePlayers() {
        return new AccountParser(Mode.ONLINE_PLAYERS);
    }

    @Override
    public @NonNull ArgumentParser<BancoCommandSource, Account> parser() {
        return this;
    }

    @Override
    public @NonNull TypeToken<Account> valueType() {
        return TypeToken.get(Account.class);
    }

    @Override
    public @NonNull Iterable<@NonNull String> stringSuggestions(@NonNull CommandContext<BancoCommandSource> commandContext,
            @NonNull CommandInput input) {

        return switch (mode) {
            case ONLINE_PLAYERS -> Bukkit.getServer().getOnlinePlayers().stream()
                .filter(player -> Banco.get().getAccountManager().getByUuid(player.getUniqueId()) != null)
                .map(Player::getName)
                .toList();
            default -> Banco.get().getAccountManager().getDatabase().getAccountIdentifierCache().stream()
                .map(AccountIdentifierKey::name)
                .toList();
        };
    }

    @Override
    public @NonNull ArgumentParseResult<@NonNull Account> parse(
            @NonNull CommandContext<@NonNull BancoCommandSource> commandContext, @NonNull CommandInput commandInput) {

        final String input = commandInput.readString();
        final Account account = Banco.get().getAccountManager().getByName(input);

        if (account == null)
            return ArgumentParseResult.failure(new UnknownAccountException(input, this, commandContext));

        return ArgumentParseResult.success(account);
    }

    public static enum Mode {
        ALL,
        ONLINE_PLAYERS
    }

}
