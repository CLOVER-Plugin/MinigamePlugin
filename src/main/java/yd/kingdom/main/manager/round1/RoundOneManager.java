package yd.kingdom.main.manager.round1;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import yd.kingdom.main.Main;

public class RoundOneManager {
    private final Main plugin;

    public RoundOneManager(Main plugin) {
        this.plugin = plugin;
    }

    public void start() {
        Bukkit.getLogger().info("1라운드: 그림맞추기 시작");
        Location tpLocation = new Location(Bukkit.getWorld("world"), 28, -55, -114);
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getGameMode() != GameMode.SPECTATOR) {
                player.teleport(tpLocation);
            }
        }
    }
}