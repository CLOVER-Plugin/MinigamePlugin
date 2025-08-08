package yd.kingdom.main.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import yd.kingdom.main.Main;
import yd.kingdom.main.game.TeamManager;

public class TeamDefendListener implements Listener {

    private final TeamManager tm = TeamManager.getInstance();

    public TeamDefendListener() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player p : tm.getDefendTeam()) {
                    p.addPotionEffect(new PotionEffect(
                            PotionEffectType.REGENERATION,
                            40,2,true,false
                    ));
                }
            }
        }.runTaskTimer(Main.getInstance(), 0L, 20*2);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player p)) return;
        if (e.getCause() == EntityDamageEvent.DamageCause.FALL && tm.isDefendTeam(p)) {
            e.setCancelled(true);
        }
    }

}
