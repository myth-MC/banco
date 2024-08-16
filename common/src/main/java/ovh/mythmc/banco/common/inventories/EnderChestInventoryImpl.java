package ovh.mythmc.banco.common.inventories;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.inventories.BancoInventory;
import ovh.mythmc.banco.api.items.BancoItem;
import ovh.mythmc.banco.common.util.ItemUtil;

import java.math.BigDecimal;
import java.util.UUID;

import static ovh.mythmc.banco.common.impl.BancoHelperImpl.convertAmountToItems;

public final class EnderChestInventoryImpl implements BancoInventory<Inventory> {

    @Override
    public @NotNull Inventory get(UUID uuid) {
        return Bukkit.getPlayer(uuid).getEnderChest();
    }

    @Override
    public BigDecimal add(UUID uuid, BigDecimal amount) {
        BigDecimal amountGiven = BigDecimal.valueOf(0);

        for (ItemStack item : convertAmountToItems(amount)) {
            BancoItem bancoItem = ItemUtil.getBancoItem(item);
            if (bancoItem != null)
                amountGiven = amountGiven.add(Banco.get().getEconomyManager().value(bancoItem, item.getAmount()));

            Player player = Bukkit.getPlayer(uuid);
            if (!player.getEnderChest().addItem(item).isEmpty())
                player.getWorld().dropItemNaturally(player.getLocation(), item);
        }

        return amountGiven;
    }

    @Override
    public BigDecimal remove(UUID uuid, BigDecimal amount) {
        return null;
    }

}
