package yd.kingdom.main.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import yd.kingdom.main.game.SoccerBallManager;
import yd.kingdom.main.util.LocationUtil;
import yd.kingdom.main.util.MessageUtil;

public class SoccerBallCommand implements CommandExecutor {
    private final SoccerBallManager manager = SoccerBallManager.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = (Player) sender;
        if (args.length != 1) {
            MessageUtil.send(player, "사용법: /축구공 <소환 | 소환위치 | 해제 | 골대표시 | 골대제거 | 상태>");
            return false;
        }
        switch (args[0].toLowerCase()) {
            case "소환":
                manager.spawnBall(player);
                manager.enableAutoRespawn(); // 자동 재소환 활성화
                MessageUtil.send(player, "§a축구공을 소환하고 자동 재소환을 활성화했습니다.");
                break;
            case "소환위치":
                manager.spawnBallAtSpawnLocation();
                manager.enableAutoRespawn(); // 자동 재소환 활성화
                MessageUtil.send(player, "§a설정된 위치에 축구공을 소환하고 자동 재소환을 활성화했습니다.");
                break;
            case "해제":
                manager.clearBalls();
                manager.disableAutoRespawn(); // 자동 재소환 비활성화
                MessageUtil.send(player, "§a모든 축구공을 제거하고 자동 재소환을 비활성화했습니다.");
                break;
            case "골대표시":
                showGoalAreas(player);
                break;
            case "골대제거":
                removeGoalAreas(player);
                break;
            case "상태":
                showStatus(player);
                break;
            case "좌표테스트":
                testCoordinates(player);
                break;
            default:
                MessageUtil.send(player, "사용법: /축구공 <소환 | 소환위치 | 해제 | 골대표시 | 골대제거 | 상태 | 좌표테스트>");
                MessageUtil.send(player, "  소환: 플레이어 앞에 축구공 소환");
                MessageUtil.send(player, "  소환위치: config.yml에 설정된 위치에 축구공 소환");
                MessageUtil.send(player, "  해제: 모든 축구공 제거");
                MessageUtil.send(player, "  골대표시: 골대 영역을 블록으로 표시");
                MessageUtil.send(player, "  골대제거: 골대 표시 블록 제거");
                MessageUtil.send(player, "  상태: 현재 축구공 및 자동 재소환 상태 확인");
                MessageUtil.send(player, "  좌표테스트: config.yml 좌표와 실제 좌표 비교");
                return false;
        }
        return true;
    }

    /**
     * 골대 영역을 블록으로 시각화
     */
    private void showGoalAreas(Player player) {
        // 골대1 영역 표시
        if (LocationUtil.getGoal1Point1() != null && LocationUtil.getGoal1Point2() != null) {
            showRectangleArea(player, LocationUtil.getGoal1Point1(), LocationUtil.getGoal1Point2(), Material.RED_WOOL);
            MessageUtil.send(player, "§a골대1 영역을 빨간 양털로 표시했습니다.");
        }

        // 골대2 영역 표시
        if (LocationUtil.getGoal2Point1() != null && LocationUtil.getGoal2Point2() != null) {
            showRectangleArea(player, LocationUtil.getGoal2Point1(), LocationUtil.getGoal2Point2(), Material.BLUE_WOOL);
            MessageUtil.send(player, "§a골대2 영역을 파란 양털로 표시했습니다.");
        }
    }

    /**
     * 골대 영역 표시 블록 제거
     */
    private void removeGoalAreas(Player player) {
        // 골대1 영역 제거
        if (LocationUtil.getGoal1Point1() != null && LocationUtil.getGoal1Point2() != null) {
            clearRectangleArea(LocationUtil.getGoal1Point1(), LocationUtil.getGoal1Point2());
        }

        // 골대2 영역 제거
        if (LocationUtil.getGoal2Point1() != null && LocationUtil.getGoal2Point2() != null) {
            clearRectangleArea(LocationUtil.getGoal2Point1(), LocationUtil.getGoal2Point2());
        }

        MessageUtil.send(player, "§a골대 영역 표시를 제거했습니다.");
    }

    /**
     * 직사각형 영역을 블록으로 표시
     */
    private void showRectangleArea(Player player, Location point1, Location point2, Material material) {
        int minX = Math.min(point1.getBlockX(), point2.getBlockX());
        int maxX = Math.max(point1.getBlockX(), point2.getBlockX());
        int minZ = Math.min(point1.getBlockZ(), point2.getBlockZ());
        int maxZ = Math.max(point1.getBlockZ(), point2.getBlockZ());
        int y = point1.getBlockY();

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                player.getWorld().getBlockAt(x, y, z).setType(material);
            }
        }
    }

    /**
     * 직사각형 영역의 블록을 제거
     */
    private void clearRectangleArea(Location point1, Location point2) {
        int minX = Math.min(point1.getBlockX(), point2.getBlockX());
        int maxX = Math.max(point1.getBlockX(), point2.getBlockX());
        int minZ = Math.min(point1.getBlockZ(), point2.getBlockZ());
        int maxZ = Math.max(point1.getBlockZ(), point2.getBlockZ());
        int y = point1.getBlockY();

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                point1.getWorld().getBlockAt(x, y, z).setType(Material.AIR);
            }
        }
    }

    /**
     * 축구공 및 자동 재소환 상태 표시
     */
    private void showStatus(Player player) {
        if (manager.getBallCount() == 0) {
            MessageUtil.send(player, "§c현재 축구공이 없습니다.");
        } else {
            MessageUtil.send(player, "§a현재 축구공: " + manager.getBallCount() + "개");
        }

        if (manager.isAutoRespawnEnabled()) {
            MessageUtil.send(player, "§a자동 재소환이 활성화되어 있습니다.");
        } else {
            MessageUtil.send(player, "§c자동 재소환이 비활성화되어 있습니다.");
        }
    }

    private void testCoordinates(Player player) {
        Location cfg = LocationUtil.getSoccerBallSpawnLocation();
        if (cfg == null) {
            MessageUtil.send(player, "§cconfig.yml의 soccer_ball.spawn_location 이 설정되지 않았습니다.");
            return;
        }

        String worldName = (cfg.getWorld() != null) ? cfg.getWorld().getName() : "null";
        String msg = String.format("§aConfig 소환 좌표 → world=%s, x=%.3f, y=%.3f, z=%.3f", worldName, cfg.getX(), cfg.getY(), cfg.getZ());
        MessageUtil.send(player, msg);
        Bukkit.getLogger().info("[SoccerBall] " + msg.replace('§', ' '));
    }
}