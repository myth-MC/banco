package ovh.mythmc.banco.common.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.economy.BancoItem;

public class EntityDeathListener implements Listener {

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntityType().equals(EntityType.PLAYER))
            return;

        event.getDrops().removeIf(item -> {
            String materialName = item.getType().name();
            String displayName = null;
            Integer customModelData = null;

            if (item.hasItemMeta()) {
                displayName = item.getItemMeta().getDisplayName();
                if (item.getItemMeta().hasCustomModelData())
                    customModelData = item.getItemMeta().getCustomModelData();
            }

            BancoItem bancoItem = Banco.get().getEconomyManager().get(materialName, displayName, customModelData);
            return bancoItem != null;
        });
    }

}
