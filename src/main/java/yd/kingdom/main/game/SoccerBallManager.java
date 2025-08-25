package yd.kingdom.main.game;


import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import yd.kingdom.main.Main;
import yd.kingdom.main.util.LocationUtil;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

public class SoccerBallManager {
    private static SoccerBallManager instance;
    private final Set<Ball> balls = Collections.synchronizedSet(new LinkedHashSet<>());
    private static final int FLOOR_BLOCK_Y = -61;   // 바닥 블록 Y(정수)
    private static final double PITCH_Y = FLOOR_BLOCK_Y + 0.4; // 블록 윗면보다 살짝 위
    private boolean autoRespawnEnabled = false; // 자동 재소환 활성화 상태

    private SoccerBallManager() {}

    public static SoccerBallManager getInstance() {
        if (instance == null) instance = new SoccerBallManager();
        return instance;
    }

    /** 매틱 물리 시작 (onEnable에서 한 번만 호출) */
    public void startPhysics(Main plugin) {
        new BukkitRunnable() {
            @Override
            public void run() {
                balls.removeIf(b -> !b.entity.isValid());

                for (Ball b : balls) {
                    Location loc = b.entity.getLocation();
                    World world = loc.getWorld();

                    // Y 좌표를 PITCH_Y로 설정
                    loc.setY(PITCH_Y);

                    // 골대 감지 및 자동 처리
                    if (LocationUtil.isInGoal(loc)) {
                        Bukkit.getLogger().info("[SoccerBall] 골대 감지됨! 위치: " + loc.getX() + ", " + loc.getY() + ", " + loc.getZ());
                        Bukkit.getLogger().info("[SoccerBall] 자동 재소환 상태: " + autoRespawnEnabled);
                        handleGoal(b);
                        continue;
                    }

                    // 2) 다음 X/Z 좌표 계산
                    Vector vel = b.velocity;
                    double nextX = loc.getX() + vel.getX();
                    double nextZ = loc.getZ() + vel.getZ();

                    // 3) 충돌 검사: 땅 바로 위(groundBlockY+1)에 벽이 있으면 그 방향 차단
                    int checkY = FLOOR_BLOCK_Y + 1;

                    int bx = loc.getBlockX(), bz = loc.getBlockZ();
                    int nBX = (int) Math.floor(nextX);
                    int nBZ = (int) Math.floor(nextZ);

                    boolean blockedX = !world.getBlockAt(nBX, checkY, bz).isPassable();
                    boolean blockedZ = !world.getBlockAt(bx, checkY, nBZ).isPassable();

                    // 4) 막힌 방향 속도 0, 자유 방향만 이동
                    if (blockedX) vel.setX(0); else loc.setX(nextX);
                    if (blockedZ) vel.setZ(0); else loc.setZ(nextZ);

                    // 5) 마찰 적용
                    vel.multiply(0.85);
                    if (vel.lengthSquared() < 1e-4) { vel.setX(0); vel.setZ(0); }

                    // 6) 엔티티 위치 갱신
                    b.entity.teleport(loc);
                }
            }
        }.runTaskTimer(plugin, 1, 1);
    }

    /**
     * 골이 들어갔을 때 처리
     */
    private void handleGoal(Ball ball) {
        Bukkit.getLogger().info("[SoccerBall] handleGoal 호출됨 - 자동 재소환 상태: " + autoRespawnEnabled);
        
        // 골 메시지 전송
        Bukkit.broadcastMessage("§a⚽ [축구] 골! 축구공이 골대에 들어갔습니다!");
        
        // 공 제거
        if (ball.entity.isValid()) {
            ball.entity.remove();
        }
        balls.remove(ball);
        
        Bukkit.getLogger().info("[SoccerBall] 공 제거 완료, 남은 공 개수: " + balls.size());
        
        // 자동 재소환이 활성화된 경우에만 새로운 공 소환
        if (autoRespawnEnabled) {
            Bukkit.getLogger().info("[SoccerBall] 자동 재소환 시작 - 3초 후 소환 예정");
            // 3초 후 새로운 공 소환
            new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.getLogger().info("[SoccerBall] 자동 재소환 실행");
                    spawnBallAtSpawnLocation();
                }
            }.runTaskLater(Main.getInstance(), 60); // 3초 후 (20틱 * 3)
        } else {
            Bukkit.getLogger().info("[SoccerBall] 자동 재소환 비활성화 상태");
        }
    }

    /**
     * 설정된 소환 위치에 축구공 소환
     */
    public void spawnBallAtSpawnLocation() {
        Location spawnLoc = LocationUtil.getSoccerBallSpawnLocation();
        if (spawnLoc == null) {
            Bukkit.getLogger().warning("[SoccerBallManager] 축구공 소환 위치가 설정되지 않았습니다!");
            return;
        }

        World world = spawnLoc.getWorld();
        if (world == null) {
            Bukkit.getLogger().warning("[SoccerBallManager] 축구공 소환 월드를 찾을 수 없습니다!");
            return;
        }

        // 디버그: config.yml에서 읽어온 좌표 출력
        Bukkit.getLogger().info("[SoccerBall] Config에서 읽어온 좌표: " + 
            spawnLoc.getX() + ", " + spawnLoc.getY() + ", " + spawnLoc.getZ());

        // config.yml에서 설정한 X, Z 좌표는 유지하고 Y는 PITCH_Y 사용
        Location spawnLocation = spawnLoc.clone();
        spawnLocation.setY(PITCH_Y);
        
        // 디버그: 실제 소환될 좌표 출력
        Bukkit.getLogger().info("[SoccerBall] 실제 소환될 좌표: " + 
            spawnLocation.getX() + ", " + spawnLocation.getY() + ", " + spawnLocation.getZ());

        // ArmorStand 생성
        ArmorStand as = (ArmorStand) world.spawnEntity(spawnLocation, EntityType.ARMOR_STAND);
        
        // 디버그: 실제 생성된 엔티티의 좌표 출력
        Location actualLocation = as.getLocation();
        Bukkit.getLogger().info("[SoccerBall] 실제 생성된 엔티티 좌표: " + 
            actualLocation.getX() + ", " + actualLocation.getY() + ", " + actualLocation.getZ());
        
        as.setVisible(false);
        as.setGravity(false);
        as.setMarker(false);
        as.setSmall(true);
        as.setBasePlate(false);
        as.setInvulnerable(true);
        as.getScoreboardTags().add("soccerball");
        as.getEquipment().setHelmet(createSoccerHead());

        balls.add(new Ball(as, new Vector(0, 0, 0)));
    }

    /**
     * 자동 재소환 활성화
     */
    public void enableAutoRespawn() {
        this.autoRespawnEnabled = true;
    }

    /**
     * 자동 재소환 비활성화
     */
    public void disableAutoRespawn() {
        this.autoRespawnEnabled = false;
    }

    /**
     * 자동 재소환 상태 확인
     */
    public boolean isAutoRespawnEnabled() {
        return autoRespawnEnabled;
    }

    /**
     * 현재 축구공 개수 반환
     */
    public int getBallCount() {
        return balls.size();
    }

    /** 플레이어 머리 스컬 메타로 '축구공' 스킨 생성 */
    private ItemStack createSoccerHead() {
        // 1) 빈 헤드 아이템
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        if (meta == null) return head;

        // 2) 프로필 생성 (무작위 UUID)
        PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());

        // 3) textures 세팅
        PlayerTextures textures = profile.getTextures();
        try {
            // 디코딩한 축구공 스킨 URL
            URL skinUrl = new URL(
                    "http://textures.minecraft.net/texture/" +
                            "59f512ed6a2ac7cd5507dd936b6ac2e708c40109e728f9a23b5015fc67915"
            );
            textures.setSkin(skinUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        profile.setTextures(textures);

        // 4) 메타에 프로필 적용
        meta.setOwnerProfile(profile);
        head.setItemMeta(meta);
        return head;
    }

    /** 공 소환 (기존 방식 - 플레이어 앞에 소환) */
    public void spawnBall(Player player) {
        World world = player.getWorld();
        // 1) 플레이어 위치 & 바라보는 방향(수평)
        Location eye = player.getLocation();
        Vector dir = eye.getDirection().setY(0).normalize();

        // 2) 플레이어 앞으로 2블럭 지점
        Location front = eye.clone().add(dir.multiply(2));

        // 3) 해당 XZ에서 지면 높이 조사
        int bx = front.getBlockX();
        int bz = front.getBlockZ();

        // 4) 블럭 중앙 + 반블럭 위
        Location spawnLoc = new Location(world, bx + 0.5, PITCH_Y, bz + 0.5);

        // 5) ArmorStand 세팅
        ArmorStand as = (ArmorStand) world.spawnEntity(spawnLoc, EntityType.ARMOR_STAND);
        as.setVisible(false);
        as.setGravity(false);
        as.setMarker(false);
        as.setSmall(true);
        as.setBasePlate(false);
        as.setInvulnerable(true);
        as.getScoreboardTags().add("soccerball");      // 태그 추가
        as.getEquipment().setHelmet(createSoccerHead());

        balls.add(new Ball(as, new Vector(0, 0, 0)));
        player.sendMessage("§a[오합지졸] 축구공을 소환했습니다.");
    }

    public void handlePlayerCollision(Player player) {
        Vector dir = player.getVelocity().clone().setY(0);
        if (dir.lengthSquared() < 1e-4) return;
        dir.normalize().multiply(0.6);

        Location pLoc = player.getLocation();
        for (Ball b : balls) {
            double dist2 = b.entity.getLocation().distanceSquared(pLoc);
            if (dist2 < 2.25) {
                //Bukkit.getLogger().info("[BallKick] handlePlayerCollision: dist2=" + dist2);
                b.velocity.setX(dir.getX());
                b.velocity.setZ(dir.getZ());
            }
        }
    }

    /** 공 제거 */
    public void clearBalls() {
        for (Ball b : balls) {
            if (b.entity.isValid()) b.entity.remove();
        }
        balls.clear();
    }

    public void kickBall(ArmorStand as, Vector newVelocity) {
        for (Ball b : balls) {
            if (b.entity.getUniqueId().equals(as.getUniqueId())) {
                // Y는 쓰지 않고 XZ만 사용
                b.velocity.setX(newVelocity.getX());
                b.velocity.setZ(newVelocity.getZ());
                break;
            }
        }
    }

    /** 공 데이터 구조 */
    private static class Ball {
        final ArmorStand entity;
        final Vector velocity;
        Ball(ArmorStand e, Vector v) {
            this.entity = e;
            this.velocity = v;
        }
    }
}