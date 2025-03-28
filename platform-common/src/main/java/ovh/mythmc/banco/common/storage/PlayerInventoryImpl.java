package ovh.mythmc.banco.common.storage;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.banco.api.storage.BancoInventory;

import java.util.UUID;

public final class PlayerInventoryImpl extends BancoInventory {

    @Override
    public String friendlyName() {
        return "PLAYER_INVENTORY";
    }

    @Override
    public @NotNull Inventory inventory(UUID uuid) {
        return Bukkit.getPlayer(uuid).getInventory();
    }

}
