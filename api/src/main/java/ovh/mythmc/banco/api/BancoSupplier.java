package ovh.mythmc.banco.api;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@UtilityClass
public class BancoSupplier {

    private Banco banco;

    public void set(final @NotNull Banco b) {
        if (banco != null)
            throw new AlreadyInitializedException();

        banco = Objects.requireNonNull(b);
    }

    public @NotNull Banco get() { return banco; }

}
