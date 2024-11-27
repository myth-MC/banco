package ovh.mythmc.banco.common.impl.inventories;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.bukkit.util.ItemUtil;
import ovh.mythmc.banco.api.items.BancoItem;
import ovh.mythmc.banco.api.storage.BancoStorage;

public final class FallbackInventoryImpl implements BancoStorage {

    @Override
    public BigDecimal add(UUID uuid, BigDecimal amount) {
        Player player = Bukkit.getPlayer(uuid);

        dropItems(player.getLocation().add(0.5, 0.5, 0.5), ItemUtil.convertAmountToItems(amount));
        return amount;
    }

    @Override
    public BigDecimal remove(UUID uuid, BigDecimal amount) {
        return BigDecimal.valueOf(0);
    }

    private void dropItems(Location location, List<ItemStack> items) {
        if (items.isEmpty())
            return;

        Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("banco"), new Runnable() {
            @Override
            public void run() {
                location.getWorld().dropItemNaturally(location, items.getFirst());
                BancoItem bancoItem = ItemUtil.getBancoItem(items.getFirst());
                Banco.get().getLogger().info("dropping " + items.getFirst() + " at " + location + " bancoItem: " + bancoItem);
                items.removeFirst();
                dropItems(location, items);
            }
        }, 1);
    }
    
}
