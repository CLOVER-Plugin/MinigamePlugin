package yd.kingdom.main.game;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import yd.kingdom.main.Main;

import java.util.*;

public class RopeManager implements Listener {
    private static final RopeManager instance = new RopeManager();
    private final Map<Player, ArmorStand> standMap = new LinkedHashMap<>();
    private final Map<Player, Bat> batMap = new LinkedHashMap<>();
    private int taskId = -1;
    private static final double MAX_DISTANCE = 6.0;
    private static final double PULL_SPEED = 0.3;

    public RopeManager() {}
    public static RopeManager getInstance() { return instance; }

    /**
     * 연결 시작: ArmorStand를 플레이어 위치에 스폰하고 서로 Leash로 연결
     */
    public void startRope(Set<Player> players) {
        stopRope();
        standMap.clear();
        batMap.clear();

        // 팀원 정렬
        List<Player> list = new ArrayList<>(players);
        Collections.sort(list, Comparator.comparing(Player::getName));

        // 각 플레이어 위치에 ArmorStand 생성
        for (Player p : list) {
            Location loc = p.getLocation().clone().add(0, 0.8, 0);
            ArmorStand as = (ArmorStand) p.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
            as.setVisible(false);
            as.setGravity(false);
            as.setMarker(true);
            as.setInvulnerable(true);

            Bat bat = (Bat) p.getWorld().spawnEntity(loc, EntityType.BAT);
            bat.setInvisible(true);
            bat.setAI(false);
            bat.setSilent(true);
            bat.setGravity(false);
            bat.setInvulnerable(true);
            as.addPassenger(bat);

            standMap.put(p, as);
            batMap.put(p, bat);
        }

        // 매 틱마다 위치 동기화
        taskId = new BukkitRunnable() {
            @Override public void run() {
                // 위치 갱신: ArmorStand와 Bat 모두
                for (Player p : list) {
                    ArmorStand as = standMap.get(p);
                    Bat bat = batMap.get(p);
                    if (p.isValid() && as != null && as.isValid() && bat != null && bat.isValid()) {
                        Location dest = p.getLocation().clone().add(0, 0.8, 0);
                        as.teleport(dest);
                        bat.teleport(dest);
                    }
                }
                // Bat 간 Leash 재적용
                List<Bat> bats = new ArrayList<>(batMap.values());
                for (int i = 0; i < bats.size() - 1; i++) {
                    Bat a = bats.get(i);
                    Bat b = bats.get(i + 1);
                    if (a.isValid() && b.isValid()) {
                        a.setLeashHolder(b);
                    }
                }
            }
        }.runTaskTimer(Main.getInstance(), 1, 1).getTaskId();
    }

    /**
     * 연결 해제: 스케줄 취소 후 모든 ArmorStand 제거
     */
    public void stopRope() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            taskId = -1;
        }
        for (Bat bat : batMap.values()) if (bat.isValid()) bat.remove();
        for (ArmorStand as : standMap.values()) if (as.isValid()) as.remove();
        standMap.clear();
        batMap.clear();
    }

    /**
     * 플레이어 이동 처리: 이웃과 거리가 MAX_DISTANCE 초과 시,
     * 부드러운 당김을 위해 velocity를 설정
     */
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (standMap.isEmpty()) return;
        List<Player> list = new ArrayList<>(standMap.keySet());
        Collections.sort(list, Comparator.comparing(Player::getName));

        for (int i = 0; i < list.size() - 1; i++) {
            Player a = list.get(i);
            Player b = list.get(i + 1);
            Location la = a.getLocation();
            Location lb = b.getLocation();
            double dist = la.distance(lb);
            if (dist > MAX_DISTANCE) {
                // a, b 양쪽 모두 부드럽게 당김
                Vector vecAB = lb.toVector().subtract(la.toVector());
                if (vecAB.lengthSquared() > 1e-6) {
                    Vector dirAB = vecAB.normalize();
                    a.setVelocity(dirAB.multiply(PULL_SPEED));
                }
                Vector vecBA = la.toVector().subtract(lb.toVector());
                if (vecBA.lengthSquared() > 1e-6) {
                    Vector dirBA = vecBA.normalize();
                    b.setVelocity(dirBA.multiply(PULL_SPEED));
                }
            }
        }
    }

    // 로프용 엔티티 피해 무시
    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        org.bukkit.entity.Entity ent = e.getEntity();
        if (ent instanceof ArmorStand || ent instanceof Bat) {
            e.setCancelled(true);
        }
    }


    @EventHandler
    public void onToggleSneak(PlayerToggleSneakEvent e) {
        Player p = e.getPlayer();
        if (standMap.containsKey(p)) {
            e.setCancelled(true);
            p.setSneaking(false);
        }
    }
}