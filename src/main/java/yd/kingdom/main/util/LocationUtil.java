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