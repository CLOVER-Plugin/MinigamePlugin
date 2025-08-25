package yd.kingdom.main.util;

import org.bukkit.Location;
import yd.kingdom.main.Main;

public class LocationUtil {
    private static Location corner1;
    private static Location corner2;
    private static Location canvas;
    private static Location zombieSpawn;
    private static Location jail;
    private static Location release;
    private static Location soccerBallSpawn;
    private static Location goal1Point1;
    private static Location goal1Point2;
    private static Location goal2Point1;
    private static Location goal2Point2;

    /**
     * config.yml에서 좌표를 읽어와 모든 위치 값을 초기화합니다.
     */
    public static void loadFromConfig(Main plugin) {
        var cfg = plugin.getConfig();

        // 캔버스 코너 좌표
        Location c1 = new Location(
                plugin.getServer().getWorld(cfg.getString("canvas.corner1.world")),
                cfg.getDouble("canvas.corner1.x"),
                cfg.getDouble("canvas.corner1.y"),
                cfg.getDouble("canvas.corner1.z")
        );
        Location c2 = new Location(
                plugin.getServer().getWorld(cfg.getString("canvas.corner2.world")),
                cfg.getDouble("canvas.corner2.x"),
                cfg.getDouble("canvas.corner2.y"),
                cfg.getDouble("canvas.corner2.z")
        );
        setCanvasCorners(c1, c2);

        // 좀비 소환 위치
        Location zs = new Location(
                plugin.getServer().getWorld(cfg.getString("zombie_spawn.world")),
                cfg.getDouble("zombie_spawn.x"),
                cfg.getDouble("zombie_spawn.y"),
                cfg.getDouble("zombie_spawn.z")
        );
        setZombieSpawnLocation(zs);

        // 감옥 입장/해제 위치
        Location jl = new Location(
                plugin.getServer().getWorld(cfg.getString("jail.world")),
                cfg.getDouble("jail.x"),
                cfg.getDouble("jail.y"),
                cfg.getDouble("jail.z")
        );
        Location rl = new Location(
                plugin.getServer().getWorld(cfg.getString("release.world")),
                cfg.getDouble("release.x"),
                cfg.getDouble("release.y"),
                cfg.getDouble("release.z")
        );
        setJailLocation(jl);
        setReleaseLocation(rl);

        // 축구공 소환 위치
        Location sbs = new Location(
                plugin.getServer().getWorld(cfg.getString("soccer_ball.spawn_location.world")),
                cfg.getDouble("soccer_ball.spawn_location.x"),
                cfg.getDouble("soccer_ball.spawn_location.y"),
                cfg.getDouble("soccer_ball.spawn_location.z")
        );
        
        // 디버그: config.yml에서 읽어온 축구공 소환 좌표 출력
        plugin.getLogger().info("[LocationUtil] Config에서 읽어온 축구공 소환 좌표: " + 
            cfg.getDouble("soccer_ball.spawn_location.x") + ", " + 
            cfg.getDouble("soccer_ball.spawn_location.y") + ", " + 
            cfg.getDouble("soccer_ball.spawn_location.z"));
        
        setSoccerBallSpawnLocation(sbs);

        // 골대1 두 지점
        Location g1p1 = new Location(
                plugin.getServer().getWorld(cfg.getString("soccer_ball.goal1.point1.world")),
                cfg.getDouble("soccer_ball.goal1.point1.x"),
                cfg.getDouble("soccer_ball.goal1.point1.y"),
                cfg.getDouble("soccer_ball.goal1.point1.z")
        );
        Location g1p2 = new Location(
                plugin.getServer().getWorld(cfg.getString("soccer_ball.goal1.point2.world")),
                cfg.getDouble("soccer_ball.goal1.point2.x"),
                cfg.getDouble("soccer_ball.goal1.point2.y"),
                cfg.getDouble("soccer_ball.goal1.point2.z")
        );
        setGoal1Locations(g1p1, g1p2);

        // 골대2 두 지점
        Location g2p1 = new Location(
                plugin.getServer().getWorld(cfg.getString("soccer_ball.goal2.point1.world")),
                cfg.getDouble("soccer_ball.goal2.point1.x"),
                cfg.getDouble("soccer_ball.goal2.point1.y"),
                cfg.getDouble("soccer_ball.goal2.point1.z")
        );
        Location g2p2 = new Location(
                plugin.getServer().getWorld(cfg.getString("soccer_ball.goal2.point2.world")),
                cfg.getDouble("soccer_ball.goal2.point2.x"),
                cfg.getDouble("soccer_ball.goal2.point2.y"),
                cfg.getDouble("soccer_ball.goal2.point2.z")
        );
        setGoal2Locations(g2p1, g2p2);
    }

    public static void setCanvasCorners(Location c1, Location c2) {
        corner1 = c1;
        corner2 = c2;
    }
    public static Location getCanvasCorner1() { return corner1; }
    public static Location getCanvasCorner2() { return corner2; }

    public static void setZombieSpawnLocation(Location loc) { zombieSpawn = loc; }
    public static Location getZombieSpawnLocation() { return zombieSpawn; }

    public static void setJailLocation(Location loc) { jail = loc; }
    public static Location getJailLocation() { return jail; }

    public static void setReleaseLocation(Location loc) { release = loc; }
    public static Location getReleaseLocation() { return release; }

    public static void setSoccerBallSpawnLocation(Location loc) { soccerBallSpawn = loc; }
    public static Location getSoccerBallSpawnLocation() { return soccerBallSpawn; }

    public static void setGoal1Locations(Location point1, Location point2) { 
        goal1Point1 = point1; 
        goal1Point2 = point2; 
    }
    public static Location getGoal1Point1() { return goal1Point1; }
    public static Location getGoal1Point2() { return goal1Point2; }

    public static void setGoal2Locations(Location point1, Location point2) { 
        goal2Point1 = point1; 
        goal2Point2 = point2; 
    }
    public static Location getGoal2Point2() { return goal2Point2; }
    public static Location getGoal2Point1() { return goal2Point1; }

    /**
     * 축구공이 골대에 들어갔는지 확인합니다.
     * 두 지점 사이의 직사각형 영역 내에 있는지 검사합니다.
     */
    public static boolean isInGoal(Location ballLocation) {
        // 골대1 검사
        if (goal1Point1 != null && goal1Point2 != null) {
            boolean inGoal1 = isInRectangle(ballLocation, goal1Point1, goal1Point2);
            if (inGoal1) {
                org.bukkit.Bukkit.getLogger().info("[LocationUtil] 골대1에 들어감! 공 위치: " + 
                    ballLocation.getX() + ", " + ballLocation.getY() + ", " + ballLocation.getZ());
                return true;
            }
        }
        
        // 골대2 검사
        if (goal2Point1 != null && goal2Point2 != null) {
            boolean inGoal2 = isInRectangle(ballLocation, goal2Point1, goal2Point2);
            if (inGoal2) {
                org.bukkit.Bukkit.getLogger().info("[LocationUtil] 골대2에 들어감! 공 위치: " + 
                    ballLocation.getX() + ", " + ballLocation.getY() + ", " + ballLocation.getZ());
                return true;
            }
        }
        
        return false;
    }

    /**
     * 축구공이 두 지점으로 정의된 직사각형 영역 내에 있는지 확인합니다.
     */
    private static boolean isInRectangle(Location ballLocation, Location point1, Location point2) {
        // Y 좌표는 고려하지 않음 (높이 제한 없음)
        double minX = Math.min(point1.getX(), point2.getX());
        double maxX = Math.max(point1.getX(), point2.getX());
        double minZ = Math.min(point1.getZ(), point2.getZ());
        double maxZ = Math.max(point1.getZ(), point2.getZ());
        
        double ballX = ballLocation.getX();
        double ballZ = ballLocation.getZ();
        
        return ballX >= minX && ballX <= maxX && ballZ >= minZ && ballZ <= maxZ;
    }

    public static void clearArea(Location c1, Location c2) {
        int x1 = Math.min(c1.getBlockX(), c2.getBlockX());
        int y1 = Math.min(c1.getBlockY(), c2.getBlockY());
        int z1 = Math.min(c1.getBlockZ(), c2.getBlockZ());
        int x2 = Math.max(c1.getBlockX(), c2.getBlockX());
        int y2 = Math.max(c1.getBlockY(), c2.getBlockY());
        int z2 = Math.max(c1.getBlockZ(), c2.getBlockZ());
        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                for (int z = z1; z <= z2; z++) {
                    c1.getWorld().getBlockAt(x, y, z).setType(org.bukkit.Material.AIR);
                }
            }
        }
    }
}