package ovh.mythmc.banco.common.storage;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.banco.api.storage.BancoInventory;

import java.util.UUID;

public final class PlayerInventoryImpl extends BancoInventory {

    @Override
    public @NotNull Inventory get(UUID uuid) {
        return Bukkit.getPlayer(uuid).getInventory();
    }

}
