package ovh.mythmc.banco.api.accounts.service;

import java.util.Optional;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;

public interface LocalUUIDResolver {

    @NotNull Optional<UUID> resolve(final @NotNull String username);

    @NotNull Optional<OfflinePlayerReference> resolveOfflinePlayer(final @NotNull UUID uuid);
    
}
