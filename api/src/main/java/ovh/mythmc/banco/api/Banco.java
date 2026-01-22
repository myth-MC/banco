package ovh.mythmc.banco.api;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.banco.api.configuration.BancoSettingsProvider;
import ovh.mythmc.banco.api.storage.BancoStorageRegistry;
import ovh.mythmc.banco.api.accounts.AccountManager;
import ovh.mythmc.banco.api.items.BancoItemRegistry;
import ovh.mythmc.banco.api.logger.LoggerWrapper;

/**
 * Main API interface for the Banco plugin.
 * <p>
 * This interface provides access to all major components of the Banco system:
 * <ul>
 *   <li>Account management</li>
 *   <li>Item registry</li>
 *   <li>Storage registry</li>
 *   <li>Configuration settings</li>
 *   <li>Logging</li>
 * </ul>
 * </p>
 * <p>
 * To get an instance of this interface, use {@link #get()}.
 * </p>
 *
 * @since 1.0.0
 */
public interface Banco {

    /**
     * Gets the Banco instance.
     * <p>
     * This method returns the singleton instance of Banco that was set by the plugin.
     * </p>
     *
     * @return the Banco instance
     * @throws IllegalStateException if Banco has not been initialized
     */
    @NotNull
    static Banco get() {
        return BancoSupplier.get();
    }

    /**
     * Reloads the plugin configuration and settings.
     * <p>
     * This method is marked as internal and should only be called by the plugin itself.
     * </p>
     */
    @ApiStatus.Internal
    void reload();

    /**
     * Gets the plugin version.
     *
     * @return the plugin version string
     */
    @NotNull
    String version();

    /**
     * Checks if the plugin is currently shutting down.
     *
     * @return true if shutting down, false otherwise
     */
    boolean isShuttingDown();

    /**
     * Gets the account manager.
     * <p>
     * The account manager provides methods for creating, deleting, and managing
     * player accounts and their balances.
     * </p>
     *
     * @return the account manager
     */
    @NotNull
    AccountManager getAccountManager();

    /**
     * Gets the logger wrapper.
     * <p>
     * The logger provides methods for logging messages at different levels.
     * </p>
     *
     * @return the logger wrapper
     */
    @NotNull
    LoggerWrapper getLogger();

    /**
     * Gets the settings provider.
     * <p>
     * The settings provider gives access to the plugin's configuration.
     * </p>
     *
     * @return the settings provider
     */
    @NotNull
    BancoSettingsProvider getSettings();

    /**
     * Gets the item registry.
     * <p>
     * The item registry manages all registered currency items.
     * </p>
     *
     * @return the item registry
     */
    @NotNull
    default BancoItemRegistry getItemRegistry() {
        return BancoItemRegistry.instance;
    }

    /**
     * Gets the storage registry.
     * <p>
     * The storage registry manages all registered storage systems for holding currency.
     * </p>
     *
     * @return the storage registry
     */
    @NotNull
    default BancoStorageRegistry getStorageRegistry() {
        return BancoStorageRegistry.instance;
    }
}
