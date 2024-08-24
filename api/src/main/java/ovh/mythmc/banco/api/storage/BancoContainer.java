package ovh.mythmc.banco.api.storage;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

/**
 * A container which provides a mutable list of ItemStack
 * @param <T> Must be ItemStack for now
 */
public interface BancoContainer<T> extends BancoStorage {

    /**
     *
     * @param uuid UUID of the account to get this BancoContainer from
     * @return A modifiable list of ItemStack
     */
    @NotNull List<T> get(UUID uuid);

    /**
     *
     * @return Max amount of items that this container can hold
     */
    @NotNull Integer maxSize();

}

