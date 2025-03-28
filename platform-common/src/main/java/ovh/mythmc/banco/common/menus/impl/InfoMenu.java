package ovh.mythmc.banco.common.menus.impl;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.scheduler.BancoScheduler;
import ovh.mythmc.banco.common.menus.BasicMenu;
import ovh.mythmc.banco.common.menus.MenuButton;
import ovh.mythmc.banco.common.update.UpdateChecker;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.UUID;

public final class InfoMenu extends BasicMenu {

    private static final UUID RANDOM_UUID = UUID.fromString("92864445-51c5-4c3b-9039-517c9927d1b4"); // We reuse the same "random" UUID all the time

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null, 9, Banco.get().getSettings().get().getMenus().getInfo().title());
    }

    @Override
    public void decorate() {
        addButton(0, getBancoButton());
        addButton(1, getInfoButton(Material.KNOWLEDGE_BOOK, "Server version", Bukkit.getBukkitVersion()));
        addButton(2, getInfoButton(Material.EMERALD, "Online mode", String.valueOf(Bukkit.getOnlineMode())));
        addButton(3, getInfoButton(Material.STICK, "Items", Banco.get().getItemRegistry().get().size() + ""));
        addButton(4, getInfoButton(Material.CHEST, "Storages", Banco.get().getItemRegistry().get().size() + ""));
        addButton(5, getInfoButton(Material.COMPASS, "Accounts", Banco.get().getAccountManager().get().size() + " (" + Banco.get().getAccountManager().getDatabase().getCachedAccounts().size() + " cached)"));
        addButton(6, getInfoButton(Material.CHAIN, "Transactions in Queue", BancoScheduler.get().getQueuedTransactions().size() + ""));
        if (!UpdateChecker.getLatest().equals(Banco.get().version()))
            addButton(8, getInfoButton(Material.BELL, "New version available", "v" + UpdateChecker.getLatest()));

        super.decorate();
    }

    private MenuButton getBancoButton() {
        ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
        skullMeta.setOwnerProfile(getProfile("http://textures.minecraft.net/texture/198df42f477f213ff5e9d7fa5a4cc4a69f20d9cef2b90c4ae4f29bd17287b5"));
        itemStack.setItemMeta(skullMeta);

        return getButton(itemStack, "banco", "v" + Banco.get().version());
    }

    private MenuButton getInfoButton(Material material, String key, String value) {
        return getButton(new ItemStack(material), key, value);
    }

    private MenuButton getButton(ItemStack itemStack, String key, String value) {
        String name = String.format(Banco.get().getSettings().get().getMenus().getInfo().keyFormat(), key);
        String lore = String.format(Banco.get().getSettings().get().getMenus().getInfo().valueFormat(), value);

        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(name);
        itemMeta.setLore(List.of(lore));
        itemStack.setItemMeta(itemMeta);

        return new MenuButton(itemStack) {
            @Override
            public void onClick(InventoryClickEvent event) {
                // ignored
            }
        };
    }

    private static PlayerProfile getProfile(String url) {
        PlayerProfile profile = Bukkit.createPlayerProfile(RANDOM_UUID); // Get a new player profile
        PlayerTextures textures = profile.getTextures();
        URL urlObject;
        try {
            urlObject = URI.create(url).toURL();
            //urlObject = new URL(url); // The URL to the skin, for example: https://textures.minecraft.net/texture/18813764b2abc94ec3c3bc67b9147c21be850cdf996679703157f4555997ea63a
        } catch (MalformedURLException exception) {
            throw new RuntimeException("Invalid URL", exception);
        }
        textures.setSkin(urlObject); // Set the skin of the player profile to the URL
        profile.setTextures(textures); // Set the textures back to the profile
        return profile;
    }

}
