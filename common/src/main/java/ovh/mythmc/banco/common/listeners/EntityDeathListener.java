package ovh.mythmc.banco.common.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import ovh.mythmc.banco.api.Banco;

public class EntityDeathListener implements Listener {

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (!Banco.get().getConfig().getSettings().getCurrency().getBoolean("remove-drops"))
            return;

        if (event.getEntityType().equals(EntityType.PLAYER))
            return;

        event.getDrops().removeIf(item -> Banco.get().getEconomyManager().value(item.getType().name()) > 0);
    }

}
