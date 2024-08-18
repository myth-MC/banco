package ovh.mythmc.banco.api.inventories;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Vector;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BancoInventoryManager {

    public static final BancoInventoryManager instance = new BancoInventoryManager();
    private static final Collection<BancoInventory<?>> inventories = new Vector<>(0);

    @ApiStatus.Internal
    public Collection<BancoInventory<?>> get() { return inventories; }

    public void registerInventory(final @NotNull BancoInventory<?>... bancoInventories) {
        inventories.addAll(Arrays.asList(bancoInventories));
    }

    public void unregisterInventory(final @NotNull BancoInventory<?>... bancoInventories) {
        inventories.removeAll(Arrays.asList(bancoInventories));
    }

}
