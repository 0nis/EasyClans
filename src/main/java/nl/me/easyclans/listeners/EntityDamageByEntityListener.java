package nl.me.easyclans.listeners;

import nl.me.easyclans.helpers.utils.PlayerUtils;
import org.bukkit.Effect;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EntityDamageByEntityListener implements Listener {

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player damager = (Player) event.getDamager();
            Player damagee = (Player) event.getEntity();
            if (!PlayerUtils.isFriendlyFireEnabledBetweenPlayers(damager, damagee)) {
                event.setCancelled(true);
            }
        }
    }

}
