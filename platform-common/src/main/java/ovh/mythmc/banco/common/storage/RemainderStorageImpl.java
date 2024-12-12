package ovh.mythmc.banco.common.storage;

import java.math.BigDecimal;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

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

        ItemUtil.convertAmountToItems(amount).forEach(itemStack -> {
            player.getWorld().dropItemNaturally(player.getLocation(), itemStack);
        });

        return amount;
    }

    @Override
    public BigDecimal remove(UUID uuid, BigDecimal amount) {
        return amount;
    }
    
}
