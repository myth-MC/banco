package ovh.mythmc.banco.api.scheduler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jetbrains.annotations.NotNull;

import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.accounts.Transaction;
import ovh.mythmc.banco.api.callback.transaction.BancoTransactionQueue;
import ovh.mythmc.banco.api.callback.transaction.BancoTransactionQueueCallback;

/**
 * Manages the scheduling and execution of transactions.
 * <p>
 * This class provides a queue-based system for processing transactions,
 * allowing for controlled execution order and timing. Transactions can be
 * executed synchronously or asynchronously based on configuration.
 * </p>
 *
 * @since 1.0.0
 */
public abstract class BancoScheduler {

    private static final int SHUTDOWN_TIMEOUT_SECONDS = 5;

    private final ScheduledExecutorService asyncScheduler = Executors.newScheduledThreadPool(1, r -> {
        Thread thread = new Thread(r, "banco-transaction-scheduler");
        thread.setDaemon(true);
        return thread;
    });

    private final List<Transaction> transactionQueue = new ArrayList<>();
    private final AtomicBoolean initialized = new AtomicBoolean(false);
    private final AtomicBoolean shutdown = new AtomicBoolean(false);

    private static volatile BancoScheduler instance;

    /**
     * Sets the scheduler instance.
     *
     * @param scheduler the scheduler instance to use
     * @throws IllegalArgumentException if scheduler is null
     */
    public static void set(@NotNull BancoScheduler scheduler) {
        if (scheduler == null) {
            throw new IllegalArgumentException("Scheduler cannot be null");
        }
        if (instance == null) {
            synchronized (BancoScheduler.class) {
                if (instance == null) {
                    instance = scheduler;
                }
            }
        }
    }

    /**
     * Gets the scheduler instance.
     *
     * @return the scheduler instance
     * @throws IllegalStateException if the scheduler has not been set
     */
    @NotNull
    public static BancoScheduler get() {
        if (instance == null) {
            throw new IllegalStateException("BancoScheduler has not been initialized");
        }
        return instance;
    }

    /**
     * Initializes the scheduler and starts processing transactions.
     */
    public void initialize() {
        if (!initialized.compareAndSet(false, true)) {
            return; // Already initialized
        }
        runTask();
    }

    /**
     * Executes a runnable synchronously on the main server thread.
     *
     * @param runnable the task to execute
     * @throws IllegalArgumentException if runnable is null
     */
    public abstract void run(@NotNull Runnable runnable);

    /**
     * Executes a runnable asynchronously.
     *
     * @param runnable the task to execute
     * @throws IllegalArgumentException if runnable is null
     */
    public abstract void runAsync(@NotNull Runnable runnable);

    /**
     * Gets a copy of all queued transactions.
     *
     * @return an unmodifiable list of queued transactions
     */
    @NotNull
    public List<Transaction> getQueuedTransactions() {
        synchronized (transactionQueue) {
            return Collections.unmodifiableList(new ArrayList<>(transactionQueue));
        }
    }

    /**
     * Queues a transaction for processing.
     *
     * @param transaction the transaction to queue
     * @throws IllegalArgumentException if transaction is null
     */
    public void queueTransaction(@NotNull Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null");
        }

        if (shutdown.get()) {
            return; // Don't queue transactions during shutdown
        }

        final var callback = new BancoTransactionQueue(transaction.asImmutable(), transactionQueue.size() + 1);
        BancoTransactionQueueCallback.INSTANCE.invoke(callback);

        if (callback.cancelled()) {
            return;
        }

        synchronized (transactionQueue) {
            transactionQueue.add(transaction);
        }
    }

    /**
     * Removes any queued transactions for the given account.
     * <p>
     * This is best-effort: it helps avoid old queued operations from running after
     * a synchronous transaction has been performed for the same account.
     * </p>
     *
     * @param uuid the UUID of the account
     * @return number of removed transactions
     * @throws IllegalArgumentException if uuid is null
     */
    public synchronized int cancelQueuedTransactionsFor(@NotNull UUID uuid) {
        if (uuid == null) {
            throw new IllegalArgumentException("UUID cannot be null");
        }

        final int before = transactionQueue.size();
        transactionQueue.removeIf(t -> {
            try {
                return t.asImmutable().account().getUuid().equals(uuid);
            } catch (Exception e) {
                // Best-effort: if we can't check, don't remove
                return false;
            }
        });
        final int after = transactionQueue.size();
        final int removed = before - after;

        try {
            if (removed > 0) {
                Banco.get().getLogger().info("BancoScheduler: removed {} queued transactions for acct={}", removed, uuid);
            }
        } catch (Exception ignored) {
            // Logging failed - continue anyway
        }

        return removed;
    }

    /**
     * Terminates the scheduler and processes all remaining transactions.
     */
    public void terminate() {
        if (!shutdown.compareAndSet(false, true)) {
            return; // Already terminated
        }

        // Shutdown the executor
        asyncScheduler.shutdown();
        try {
            if (!asyncScheduler.awaitTermination(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                asyncScheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            asyncScheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }

        // Process all remaining transactions synchronously
        final List<Transaction> remainingTransactions;
        synchronized (transactionQueue) {
            remainingTransactions = new ArrayList<>(transactionQueue);
            transactionQueue.clear();
        }

        for (final Transaction transaction : remainingTransactions) {
            try {
                transaction.transact();
            } catch (Exception e) {
                Banco.get().getLogger().error("Error processing transaction during shutdown: {}", e);
            }
        }
    }

    /**
     * Schedules the next transaction processing task.
     */
    private void runTask() {
        if (shutdown.get()) {
            return;
        }

        final long delayTicks = Banco.get().getSettings().get().getTaskQueueDelay();
        final long delayMillis = delayTicks * 50L; // Convert ticks to milliseconds

        asyncScheduler.schedule(() -> {
            if (shutdown.get()) {
                return;
            }

            processNextTransaction();
            runTask(); // Schedule next iteration
        }, delayMillis, TimeUnit.MILLISECONDS);
    }

    /**
     * Processes the next transaction in the queue.
     */
    private void processNextTransaction() {
        final Transaction transaction;

        synchronized (transactionQueue) {
            if (transactionQueue.isEmpty()) {
                return;
            }
            transaction = transactionQueue.removeFirst();
        }

        final ExecutionOrder executionOrder = Banco.get().getSettings().get().getTaskExecutionOrder();

        switch (executionOrder) {
            case SYNC -> run(() -> {
                try {
                    transaction.transact();
                    transaction.executeAfterTransaction().forEach(runnable -> {
                        try {
                            runnable.run();
                        } catch (Exception e) {
                            Banco.get().getLogger().error("Error executing post-transaction callback: {}", e);
                        }
                    });
                } catch (Exception e) {
                    Banco.get().getLogger().error("Error processing transaction: {}", e);
                }
            });
            case ASYNC -> runAsync(() -> {
                try {
                    transaction.transact();
                    transaction.executeAfterTransaction().forEach(runnable -> {
                        try {
                            runnable.run();
                        } catch (Exception e) {
                            Banco.get().getLogger().error("Error executing post-transaction callback: {}", e);
                        }
                    });
                } catch (Exception e) {
                    Banco.get().getLogger().error("Error processing transaction: {}", e);
                }
            });
        }
    }
}
