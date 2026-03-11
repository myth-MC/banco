package ovh.mythmc.banco.api;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Utility class for managing the Banco singleton instance.
 * <p>
 * This class provides thread-safe access to the Banco instance.
 * The instance can only be set once and must not be null.
 * </p>
 *
 * @since 1.0.0
 */
@UtilityClass
public class BancoSupplier {

    private volatile Banco instance;

    /**
     * Sets the Banco instance.
     * <p>
     * This method can only be called once. Subsequent calls will throw
     * an {@link AlreadyInitializedException}.
     * </p>
     *
     * @param banco the Banco instance to set
     * @throws IllegalArgumentException if banco is null
     * @throws AlreadyInitializedException if an instance has already been set
     */
    public void set(@NotNull Banco banco) {
        Objects.requireNonNull(banco, "Banco instance cannot be null");

        if (instance != null) {
            throw new AlreadyInitializedException("Banco has already been initialized");
        }

        synchronized (BancoSupplier.class) {
            if (instance != null) {
                throw new AlreadyInitializedException("Banco has already been initialized");
            }
            instance = banco;
        }
    }

    /**
     * Gets the Banco instance.
     *
     * @return the Banco instance
     * @throws IllegalStateException if no instance has been set
     */
    @NotNull
    public Banco get() {
        if (instance == null) {
            throw new IllegalStateException("Banco has not been initialized. Make sure the plugin is loaded.");
        }
        return instance;
    }
}
