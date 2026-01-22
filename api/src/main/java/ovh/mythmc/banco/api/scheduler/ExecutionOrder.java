package ovh.mythmc.banco.api.scheduler;

/**
 * Enumeration of execution orders for transaction processing.
 * <p>
 * This enum defines how transactions should be executed:
 * <ul>
 *   <li>{@link #SYNC} - Execute transactions synchronously on the main server thread</li>
 *   <li>{@link #ASYNC} - Execute transactions asynchronously on a separate thread</li>
 * </ul>
 * </p>
 *
 * @since 1.0.0
 */
public enum ExecutionOrder {

    /**
     * Execute transactions synchronously on the main server thread.
     * <p>
     * This is the default and recommended option for most servers. It ensures
     * thread safety and compatibility with Bukkit APIs that require main thread access.
     * </p>
     */
    SYNC,

    /**
     * Execute transactions asynchronously on a separate thread.
     * <p>
     * This option may improve performance on large servers but can introduce
     * unexpected behavior with Bukkit APIs that require main thread access.
     * Use with caution.
     * </p>
     */
    ASYNC
}
