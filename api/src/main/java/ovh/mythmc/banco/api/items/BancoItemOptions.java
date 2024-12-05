package ovh.mythmc.banco.api.items;

import java.util.List;

public record BancoItemOptions(String displayName, List<String> lore, Integer customModelData, Boolean glowEffect, String headTextureUrl) {

    public Boolean glowEffect() {
        if (glowEffect == null)
            return false;

        return glowEffect;
    }

}
