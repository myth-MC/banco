package ovh.mythmc.banco.api.scheduler;

import io.th0rgal.oraxen.shaded.jetbrains.annotations.NotNull;

public abstract class BancoScheduler {

    private static BancoScheduler bancoScheduler;

    public static void set(@NotNull BancoScheduler s) {
        if (bancoScheduler == null)
            bancoScheduler = s;
    }

    public static BancoScheduler get() { return bancoScheduler; }

    public abstract void run(@NotNull Runnable runnable);

    public abstract void runAsync(@NotNull Runnable runnable);

}
