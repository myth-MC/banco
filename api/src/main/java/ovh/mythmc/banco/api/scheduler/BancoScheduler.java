package ovh.mythmc.banco.api.scheduler;

import java.util.ArrayList;
import java.util.List;
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
                            bancoScheduler.run(() -> {
                                transaction.transact();
                                transaction.executeAfterTransaction().forEach(Runnable::run);
                            });
                            break;
                        case ASYNC: 
                            bancoScheduler.runAsync(() -> {
                                transaction.transact();
                                transaction.executeAfterTransaction().forEach(Runnable::run);
                            });
                            break;
                    }

                    transactionQueue.removeFirst();
                }
    
                runTask();
            }
        }, Banco.get().getSettings().get().getTaskQueueDelay() * 50, TimeUnit.MILLISECONDS);
    }

}
