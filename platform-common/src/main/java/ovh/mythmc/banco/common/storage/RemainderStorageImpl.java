package ovh.mythmc.banco.common.storage;

import java.math.BigDecimal;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.items.BancoItem;
import ovh.mythmc.banco.api.storage.BancoStorage;
import ovh.mythmc.banco.api.util.ItemUtil;

public final class RemainderStorageImpl implements BancoStorage {

    @Override
    public BigDecimal value(UUID uuid) {
        return BigDecimal.valueOf(0);
    }

    @Override
    public BigDecimal add(UUID uuid, BigDecimal amount) {
        Player player = Bukkit.getPlayer(uuid);
        BigDecimal added = BigDecimal.valueOf(0);

        for (ItemStack itemStack : ItemUtil.convertAmountToItems(amount)) {
            BancoItem bancoItem = Banco.get().getItemRegistry().getByItemStack(itemStack);
            added = added.add(bancoItem.value(itemStack.getAmount()));
            player.getWorld().dropItemNaturally(player.getLocation(), itemStack);
        }

        return added;
    }

    @Override
    public BigDecimal remove(UUID uuid, BigDecimal amount) {
        return amount;
    }
    
}
