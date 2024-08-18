package ovh.mythmc.banco.api.inventories;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.event.impl.BancoInventoryRegisterEvent;
import ovh.mythmc.banco.api.event.impl.BancoInventoryUnregisterEvent;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BancoInventoryManager {

    public static final BancoInventoryManager instance = new BancoInventoryManager();
    private static final Collection<BancoInventory<?>> inventories = new Vector<>(0);

    @ApiStatus.Internal
    public Collection<BancoInventory<?>> get() { return inventories; }

    public void registerInventory(final @NotNull BancoInventory<?>... bancoInventories) {
        List<BancoInventory<?>> bancoInventoryList = Arrays.asList(bancoInventories);
        inventories.addAll(bancoInventoryList);

        // Call BancoInventoryRegisterEvent
        bancoInventoryList.forEach(bancoInventory -> Banco.get().getEventManager().publish(new BancoInventoryRegisterEvent(bancoInventory)));
    }

    public void unregisterInventory(final @NotNull BancoInventory<?>... bancoInventories) {
        List<BancoInventory<?>> bancoInventoryList = Arrays.asList(bancoInventories);
        inventories.removeAll(bancoInventoryList);

        // Call BancoInventoryUnregisterEvent
        bancoInventoryList.forEach(bancoInventory -> Banco.get().getEventManager().publish(new BancoInventoryUnregisterEvent(bancoInventory)));
    }

}
