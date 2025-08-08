package yd.kingdom.main.listener;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import yd.kingdom.main.Main;
import yd.kingdom.main.game.PositionManager;
import yd.kingdom.main.game.SoccerBallManager;
import yd.kingdom.main.game.TeamManager;
import yd.kingdom.main.position.PositionType;
import yd.kingdom.main.util.MessageUtil;

import java.util.ArrayList;
import java.util.List;

public class PositionListener implements Listener {

    // 수비팀 지속 Regen3, 낙하 데미지 무효
    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player p)) return;
        if (TeamManager.getInstance().isDefendTeam(p)) {
            if (e.getCause() == EntityDamageEvent.DamageCause.FALL) e.setCancelled(true);
            // regen 효과는 별도 스케줄러에서
        }
    }

    // 포지션 아이템 우클릭
    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        PositionType type = PositionManager.getInstance().get(p);
        if (type == null) return;

        ItemStack item = e.getItem();
        if (item == null || !item.hasItemMeta()) return;
        String name = item.getItemMeta().getDisplayName();

        if (!PositionManager.getInstance().tryUse(p)) {
            e.setCancelled(true);
            return;
        }
        
        // 골키퍼 장갑 스킬
        if (type == PositionType.GOALKEEPER && name.equals("§6골키퍼 장갑")) {
            if (!PositionManager.getInstance().tryUse(p)) {
                e.setCancelled(true);
                return;
            }
            buildWall(p);
            MessageUtil.send(p, "골키퍼 벽 생성!");
            e.setCancelled(true);
            return;
        }

        // 공격수 신발 스킬
        if (type == PositionType.ATTACKER && name.equals("§c공격수 신발")) {
            if (!PositionManager.getInstance().tryUse(p)) {
                e.setCancelled(true);
                return;
            }
            p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 10, 1, true, false));
            MessageUtil.send(p, "신속 II 10초 부여!");
            e.setCancelled(true);
            return;
        }
        
        e.setCancelled(true);
    }

    // 수비수 막대기 스킬
    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent e) {
        Player p = e.getPlayer();
        PositionType type = PositionManager.getInstance().get(p);
        if (type != PositionType.DEFENDER) return;
        Entity ent = e.getRightClicked();
        if (!(ent instanceof ArmorStand as)) return;
        if (!as.getScoreboardTags().contains("soccerball")) return;
        if (!PositionManager.getInstance().tryUse(p)) {
            e.setCancelled(true);
            return;
        }
        // 기존 킥보다 2배
        SoccerBallManager.getInstance().kickBall(as, p.getLocation().getDirection().setY(0).normalize().multiply(2.4));
        MessageUtil.send(p, "수비수 킥!");
        e.setCancelled(true);
    }

    // 골키퍼 벽 생성
    private void buildWall(Player p) {
        Location base = p.getLocation()
                .subtract(p.getLocation().getDirection().setY(0).normalize().multiply(1));
        // cross → crossProduct, 그리고 clone() 필수
        Vector dir = p.getLocation().getDirection().setY(0).normalize();
        Vector right = dir.clone().crossProduct(new Vector(0, 1, 0)).normalize();

        List<Block> placed = new ArrayList<>();
        for (int h = 0; h < 3; h++) {
            for (int w = -4; w <= 4; w++) {
                Location loc = base.clone().add(right.clone().multiply(w)).add(0, h, 0);
                Block b = loc.getBlock();
                placed.add(b);
                b.setType(Material.DIRT);
            }
        }

        // 5초 후 해제
        new BukkitRunnable() {
            @Override public void run() {
                placed.forEach(b -> b.setType(Material.AIR));
            }
        }.runTaskLater(Main.getInstance(), 20 * 5);
    }

    // 수비팀 regen 주기
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        scheduleRegen(); // 플러그인 켜질 때 한 번만 돌도록 Main.onEnable()에서 호출하세요
    }

    private void scheduleRegen() {
        new BukkitRunnable() {
            @Override public void run() {
                TeamManager.getInstance().getDefendTeam().forEach(p ->
                        p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 40, 2, true, false))
                );
            }
        }.runTaskTimer(Main.getInstance(), 0, 20*2);
    }
}