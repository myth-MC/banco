package ovh.mythmc.banco.common.impl.inventories;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.banco.api.bukkit.inventories.BancoInventoryBukkit;

import java.util.UUID;

public final class PlayerInventoryImpl extends BancoInventoryBukkit {

    @Override
    public @NotNull Inventory get(UUID uuid) {
        return Bukkit.getPlayer(uuid).getInventory();
    }

}
