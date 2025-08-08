package yd.kingdom.main.manager.round3;

import org.bukkit.Bukkit;
import yd.kingdom.main.Main;

public class RoundThreeManager {
    private final Main plugin;

    public RoundThreeManager(Main plugin) {
        this.plugin = plugin;
    }

    public void start() {
        Bukkit.getLogger().info("3라운드: 끈끈한 점프맵 시작");
    }
}