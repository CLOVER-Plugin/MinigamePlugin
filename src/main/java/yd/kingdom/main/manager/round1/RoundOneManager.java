package yd.kingdom.main.manager.round1;

import org.bukkit.Bukkit;
import yd.kingdom.main.Main;

public class RoundOneManager {
    private final Main plugin;

    public RoundOneManager(Main plugin) {
        this.plugin = plugin;
    }

    public void start() {
        Bukkit.getLogger().info("1라운드: 그림맞추기 시작");
        // 수비/공격 역할 분배
        // 제시어 로직, 그림판 세팅, 방해 아이템 관리 등 추가
    }
}