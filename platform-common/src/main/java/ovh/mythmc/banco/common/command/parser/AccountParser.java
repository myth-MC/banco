package ovh.mythmc.banco.common.command.parser;

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
import ovh.mythmc.banco.api.accounts.service.OfflinePlayerReference;
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

    public static AccountParser playerOnly() {
        return new AccountParser(Mode.PLAYER_ONLY);
    }

    public static AccountParser nonPlayerOnly() {
        return new AccountParser(Mode.NON_PLAYER_ONLY);
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
            case PLAYER_ONLY -> Banco.get().getAccountManager().getUuidResolver().references().stream()
                .filter(offlinePlayer -> Banco.get().getAccountManager().getByUuid(offlinePlayer.uuid()) != null)
                .map(OfflinePlayerReference::name)
                .toList();
            case NON_PLAYER_ONLY -> Banco.get().getAccountManager().get().stream()
                .filter(account -> account.getName() != null && Banco.get().getAccountManager().getUuidResolver().resolveOfflinePlayer(account.getUuid()).isEmpty())
                .map(Account::getName)
                .toList();
            default -> Banco.get().getAccountManager().get().stream()
                .filter(account -> account.getName() != null)
                .map(Account::getName)
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
        PLAYER_ONLY,
        NON_PLAYER_ONLY
    }

}
