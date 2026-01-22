package ovh.mythmc.banco.api;

/**
 * Exception thrown when attempting to initialize a component that has already been initialized.
 * <p>
 * This exception is typically thrown when trying to set a singleton instance
 * that has already been set, such as the Banco instance or BancoScheduler.
 * </p>
 *
 * @since 1.0.0
 */
public final class AlreadyInitializedException extends RuntimeException {

    /**
     * Creates a new AlreadyInitializedException with a default message.
     */
    public AlreadyInitializedException() {
        super("Component has already been initialized");
    }

    /**
     * Creates a new AlreadyInitializedException with a custom message.
     *
     * @param message the detail message
     */
    public AlreadyInitializedException(String message) {
        super(message);
    }

    /**
     * Creates a new AlreadyInitializedException with a custom message and cause.
     *
     * @param message the detail message
     * @param cause the cause of this exception
     */
    public AlreadyInitializedException(String message, Throwable cause) {
        super(message, cause);
    }
}
