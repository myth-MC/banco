package ovh.mythmc.banco.api.economy;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.banco.api.AlreadyInitializedException;

import java.util.Objects;

@UtilityClass
public class BancoHelperSupplier {

    private BancoHelper bancoHelper;

    public void set(final @NotNull BancoHelper b) {
        if (bancoHelper != null)
            throw new AlreadyInitializedException();

        bancoHelper = Objects.requireNonNull(b);
    }

    public @NotNull BancoHelper get() { return  bancoHelper; }

}
