package ovh.mythmc.banco.common.impl;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.economy.BancoHelper;
import ovh.mythmc.banco.api.economy.BancoHelperSupplier;
import ovh.mythmc.banco.api.inventories.BancoInventory;
import ovh.mythmc.banco.api.items.BancoItem;
import ovh.mythmc.banco.common.inventories.EnderChestInventoryImpl;
import ovh.mythmc.banco.common.inventories.PlayerInventoryImpl;
import ovh.mythmc.banco.common.util.ItemUtil;

import java.math.BigDecimal;
import java.util.UUID;

public class BancoHelperImpl implements BancoHelper {

    private final PlayerInventoryImpl playerInventory;
    private final EnderChestInventoryImpl enderChestInventory;

    public BancoHelperImpl() {
        BancoHelperSupplier.set(this);
        this.playerInventory = new PlayerInventoryImpl();
        this.enderChestInventory = new EnderChestInventoryImpl();

        // Register banco inventories
        Banco.get().getInventoryManager().registerInventory(playerInventory);
        if (Banco.get().getSettings().get().getCurrency().isCountEnderChest())
            Banco.get().getInventoryManager().registerInventory(enderChestInventory);
    }

    // Todo: unnecessary?
    @Override
    public final BigDecimal add(UUID uuid, BigDecimal amount) {
        Player player = Bukkit.getOfflinePlayer(uuid).getPlayer();
        if (player == null)
            return BigDecimal.valueOf(0);

        return amount.subtract(playerInventory.add(uuid, amount));
    }

    // Todo: unnecessary?
    // Todo: look for less valuable items first
    @Override
    public BigDecimal remove(UUID uuid, BigDecimal amount) {
        Player player = Bukkit.getOfflinePlayer(uuid).getPlayer();
        if (player == null)
            return BigDecimal.valueOf(0);

        for (BancoInventory<Inventory> inventory : Banco.get().getInventoryManager().get()) {
            if (amount.compareTo(BigDecimal.valueOf(0)) > 0)
                amount = inventory.remove(uuid, amount);
        }

        return amount;
    }

    @Override
    public boolean isOnline(UUID uuid) {
        return Bukkit.getOfflinePlayer(uuid).isOnline();
    }

    @Override
    public BigDecimal getInventoryValue(UUID uuid) {
        BigDecimal value = BigDecimal.valueOf(0);

        for (BancoInventory<Inventory> inventory : Banco.get().getInventoryManager().get()) {
            for (ItemStack item : inventory.get(uuid)) {
                if (item != null) {
                    BancoItem bancoItem = ItemUtil.getBancoItem(item);
                    if (bancoItem != null)
                        value = value.add(Banco.get().getEconomyManager().value(bancoItem, item.getAmount()));
                }
            }
        }

        return value;
    }

}
