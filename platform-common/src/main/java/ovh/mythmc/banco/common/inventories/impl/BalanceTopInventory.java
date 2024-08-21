package ovh.mythmc.banco.common.inventories.impl;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.accounts.Account;
import ovh.mythmc.banco.common.inventories.BasicInventory;
import ovh.mythmc.banco.common.inventories.InventoryButton;
import ovh.mythmc.banco.common.util.MessageUtil;
import ovh.mythmc.banco.common.util.PlayerUtil;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public final class BalanceTopInventory extends BasicInventory {

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null, 9, "ʙᴀɴᴄᴏ");
    }

    @Override
    public void decorate() {
        Map<String, BigDecimal> values = new LinkedHashMap<>();
        for (Account account : Banco.get().getAccountManager().get()) {
            String username = Bukkit.getOfflinePlayer(account.getUuid()).getName();
            values.put(username, account.amount());
        }

        int slot = 0;
        for (Map.Entry<String, BigDecimal> entry : getTopNine(values).entrySet()) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(PlayerUtil.getUuid(entry.getKey()));
            String balance = MessageUtil.format(entry.getValue()) + Banco.get().getSettings().get().getCurrency().getSymbol();

            ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
            if (player.hasPlayedBefore())
                skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(PlayerUtil.getUuid(entry.getKey())));
            skullMeta.setItemName(ChatColor.YELLOW + "" + ChatColor.BOLD + (slot + 1) + " " + ChatColor.GREEN + entry.getKey() + ChatColor.GRAY + " - " + ChatColor.GREEN + balance);
            itemStack.setItemMeta(skullMeta);

            InventoryButton button = new InventoryButton(itemStack) {
                @Override
                public void onClick(InventoryClickEvent event) {
                    // ignored
                }
            };

            this.addButton(slot, button);

            slot = slot + 1;
        }


        super.decorate();
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> getTopNine(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.comparingByValue());

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            if (entry.getKey() == null)
                continue;
            result.put(entry.getKey(), entry.getValue());
        }

        return result.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(9)
                .collect(Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }
}
