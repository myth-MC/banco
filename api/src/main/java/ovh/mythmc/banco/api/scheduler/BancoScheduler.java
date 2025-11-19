package ovh.mythmc.banco.api.scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.jetbrains.annotations.NotNull;

import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.accounts.Transaction;
import ovh.mythmc.banco.api.callback.transaction.BancoTransactionQueue;
import ovh.mythmc.banco.api.callback.transaction.BancoTransactionQueueCallback;

public abstract class BancoScheduler {

    private final ScheduledExecutorService asyncScheduler = Executors.newScheduledThreadPool(1);

    private final List<Transaction> transactionQueue = new ArrayList<>();

    private static BancoScheduler bancoScheduler;

    public static void set(@NotNull BancoScheduler s) {
        if (bancoScheduler == null)
            bancoScheduler = s;
    }

    public static BancoScheduler get() { return bancoScheduler; }

    public void initialize() {
        runTask();
    }

    public abstract void run(@NotNull Runnable runnable);

    public abstract void runAsync(@NotNull Runnable runnable);

    public List<Transaction> getQueuedTransactions() {
        return List.copyOf(transactionQueue);
    }

    public void queueTransaction(Transaction transaction) {
        var callback = new BancoTransactionQueue(transaction.asImmutable(), transactionQueue.size() + 1);
        BancoTransactionQueueCallback.INSTANCE.invoke(callback);
        if (callback.cancelled())
            return;

        transactionQueue.add(transaction);
    }

    /**
     * Remove any queued transactions for the given account.
     *
     * This is best-effort: it helps avoid old queued ops from running after we
     * performed a synchronous transaction for the same account.
     *
     * @return number of removed transactions
     */
    public synchronized int cancelQueuedTransactionsFor(final UUID uuid) {
        int before = transactionQueue.size();
        transactionQueue.removeIf(t -> t.asImmutable().account().getUuid().equals(uuid));
        int after = transactionQueue.size();
        int removed = before - after;
        try {
            Banco.get().getLogger().info("BancoScheduler: removed {} queued transactions for acct={}", removed, uuid);
        } catch (Exception ignored) {}
        return removed;
    }

    public void terminate() {
        asyncScheduler.close();
        transactionQueue.forEach(transaction -> transaction.transact());
    }

    private void runTask() {
        asyncScheduler.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!transactionQueue.isEmpty()) {
                    Transaction transaction = transactionQueue.getFirst();
                    switch (Banco.get().getSettings().get().getTaskExecutionOrder()) {
                        case SYNC: 
                            bancoScheduler.run(() -> transaction.transact());
                            break;
                        case ASYNC: 
                            bancoScheduler.runAsync(() -> transaction.transact());
                            break;
                    }

                    transactionQueue.removeFirst();
                }
    
                runTask();
            }
        }, Banco.get().getSettings().get().getTaskQueueDelay() * 50, TimeUnit.MILLISECONDS);
    }

}
