package yd.kingdom.main.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class HungerListener implements Listener {

    /** 접속 시 만복/포화 유지 */
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        p.setFoodLevel(20);
        p.setSaturation(20f);
        p.setExhaustion(0f);
    }

    /** 배고픔이 '감소'하려 할 때만 취소 (증가는 허용) */
    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent e) {
        if (!(e.getEntity() instanceof Player p)) return;

        int before = p.getFoodLevel();
        int after  = e.getFoodLevel();

        if (after < before) {
            e.setCancelled(true);   // 감소 방지
            p.setFoodLevel(20);     // 항상 만복 유지
            p.setSaturation(20f);   // 포화도 채우기
            p.setExhaustion(0f);    // 피로도 초기화
        }
    }
}