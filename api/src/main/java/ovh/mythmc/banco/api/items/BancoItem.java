package ovh.mythmc.banco.api.items;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import ovh.mythmc.banco.api.Banco;

public interface BancoItem {

    Material material();

    BigDecimal value();

    BancoItemOptions customization();

    default String getIdentifier() {
        return material().name() + "-" + value();
    }

    default boolean isSimilar(ItemStack itemStack) {
        return itemStack.isSimilar(asItemStack());
    }

    default ItemStack asItemStack(int amount) {
        ItemStack itemStack = new ItemStack(material(), amount);

        if (customization() != null) {
            ItemMeta itemMeta = itemStack.getItemMeta();

            // Apply custom display name
            if (customization().displayName() != null)
                itemMeta.setDisplayName(format(customization().displayName()));

            // Apply lore
            if (customization().lore() != null)
                itemMeta.setLore(customization().lore().stream().map(this::format).toList());

            // Apply custom model data
            if (customization().customModelData() != null)
                itemMeta.setCustomModelData(customization().customModelData());

            // Apply glow effect
            if (customization().glowEffect() != null && customization().glowEffect())
                itemMeta.addEnchant(Enchantment.LOYALTY, 1, true);

            // Apply head texture URL
            if (customization().headTextureUrl() != null && material().equals(Material.PLAYER_HEAD))
                ((SkullMeta) itemMeta).setOwnerProfile(getProfile(customization().headTextureUrl()));

            // Hide enchantments (used for glow effect)
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

            // Store item identifier in PDC
            itemMeta.getPersistentDataContainer().set(Banco.get().getItemManager().CUSTOM_ITEM_IDENTIFIER_KEY, PersistentDataType.STRING, getIdentifier());

            // Apply ItemMeta
            itemStack.setItemMeta(itemMeta);
        }

        return itemStack;
    }

    default ItemStack asItemStack() {
        return asItemStack(1);
    }

    default PlayerProfile getProfile(String textureUrl) {
        PlayerProfile profile = Bukkit.createPlayerProfile(UUID.nameUUIDFromBytes(textureUrl.getBytes())); // Get a new player profile
        PlayerTextures textures = profile.getTextures();
        URL urlObject;
        try {
            urlObject = URI.create(textureUrl).toURL(); // The URL to the skin, for example: https://textures.minecraft.net/texture/18813764b2abc94ec3c3bc67b9147c21be850cdf996679703157f4555997ea63a
        } catch (MalformedURLException exception) {
            throw new RuntimeException("Invalid URL", exception);
        }
        textures.setSkin(urlObject); // Set the skin of the player profile to the URL
        profile.setTextures(textures); // Set the textures back to the profile
        return profile;
    }

    default String format(String input) {
        Component component = MiniMessage.miniMessage().deserialize(input);
        return LegacyComponentSerializer.legacySection().serialize(component);
    } 
    
}
