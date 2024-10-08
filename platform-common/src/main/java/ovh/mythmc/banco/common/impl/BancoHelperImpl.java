package ovh.mythmc.banco.common.impl;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.bukkit.util.ItemUtil;
import ovh.mythmc.banco.api.configuration.sections.CurrencyConfig;
import ovh.mythmc.banco.api.storage.BancoContainer;
import ovh.mythmc.banco.api.storage.BancoStorage;
import ovh.mythmc.banco.api.economy.BancoHelper;
import ovh.mythmc.banco.api.economy.BancoHelperSupplier;
import ovh.mythmc.banco.api.storage.BancoInventory;
import ovh.mythmc.banco.api.items.BancoItem;
import ovh.mythmc.banco.common.impl.inventories.EnderChestInventoryImpl;
import ovh.mythmc.banco.common.impl.inventories.PlayerInventoryImpl;

import java.math.BigDecimal;
import java.util.*;

public class BancoHelperImpl implements BancoHelper {

    public BancoHelperImpl() {
        BancoHelperSupplier.set(this);

        // Register banco inventories
        CurrencyConfig.InventoryPriority inventoryPriority = Banco.get().getSettings().get().getCurrency().getInventoryPriority();
        boolean countEnderChest = Banco.get().getSettings().get().getCurrency().isCountEnderChest();

        switch (inventoryPriority) {
            case PLAYER_INVENTORY -> {
                Banco.get().getStorageManager().registerStorage(new PlayerInventoryImpl());
                if (countEnderChest)
                    Banco.get().getStorageManager().registerStorage(new EnderChestInventoryImpl());
            }
            case ENDER_CHEST -> Banco.get().getStorageManager().registerStorage(new EnderChestInventoryImpl(),
                    new PlayerInventoryImpl());
        }
    }

    @Override
    public boolean isOnline(UUID uuid) {
        return Bukkit.getOfflinePlayer(uuid).isOnline();
    }

    @Override
    public boolean isInBlacklistedWorld(UUID uuid) {
        if (!isOnline(uuid))
            return false;

        String worldName = Bukkit.getPlayer(uuid).getWorld().getName();
        return Banco.get().getSettings().get().getCurrency().getBlacklistedWorlds().contains(worldName);
    }

    @Override
    public BigDecimal getValue(UUID uuid, Collection<BancoStorage> bancoStorages) {
        BigDecimal value = BigDecimal.valueOf(0);

        for (BancoStorage storage : bancoStorages)
            if (storage instanceof BancoInventory<?> inventory) {
                for (ItemStack item : (Inventory) inventory.get(uuid))
                    if (item != null) {
                        BancoItem bancoItem = ItemUtil.getBancoItem(item);
                        if (bancoItem != null)
                            value = value.add(Banco.get().getItemManager().value(bancoItem, item.getAmount()));
                    }
            } else if (storage instanceof BancoContainer<?> container) {
                for (ItemStack item : (List<ItemStack>) container.get(uuid))
                    if (item != null) {
                        BancoItem bancoItem = ItemUtil.getBancoItem(item);
                        if (bancoItem != null)
                            value = value.add(Banco.get().getItemManager().value(bancoItem, item.getAmount()));
                    }
            }

        return value;
    }

    @Override
    public BigDecimal getValue(UUID uuid) {
        return getValue(uuid, Banco.get().getStorageManager().get());
    }

}
