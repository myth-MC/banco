package ovh.mythmc.banco.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import ovh.mythmc.banco.Banco;

public class EntityDeathListener implements Listener {

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (!Banco.getInstance().getConfig().getBoolean("currency.remove-drops"))
            return;

        if (event.getEntityType().equals(EntityType.PLAYER))
            return;

        event.getDrops().removeIf(item -> Banco.getInstance().getEconomyManager().getValue(item.getType()) > 0);
    }

}
