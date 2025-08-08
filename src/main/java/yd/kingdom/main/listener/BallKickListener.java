package yd.kingdom.main.listener;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import yd.kingdom.main.game.PositionManager;
import yd.kingdom.main.game.SoccerBallManager;
import yd.kingdom.main.position.PositionType;
import yd.kingdom.main.util.MessageUtil;

public class BallKickListener implements Listener {
    private final SoccerBallManager manager = SoccerBallManager.getInstance();

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent e) {
        if (!(e.getRightClicked() instanceof ArmorStand as)) return;
        // 'soccerball' 태그가 있어야 우리 공
        if (!as.getScoreboardTags().contains("soccerball")) return;

        // 기본 상호작용(헬멧 줍기) 막기
        e.setCancelled(true);
        //Bukkit.getLogger().info("[BallKick] clicked soccerball by " + e.getPlayer().getName());
        Player p = e.getPlayer();
        ItemStack hand = p.getInventory().getItemInMainHand();

        // 킥 속도 계산
        Vector dir = p.getLocation().getDirection().setY(0).normalize();

        double basePower = 1.2;

        PositionType pos = PositionManager.getInstance().get(p);
        if (pos == PositionType.DEFENDER && hand.hasItemMeta() && "§b수비수 막대기".equals(hand.getItemMeta().getDisplayName())) {
            if (!PositionManager.getInstance().tryUse(p)) {
                return;
            }
            basePower *= 2;
        }

        Vector impulse = dir.multiply(basePower);
        manager.kickBall(as, impulse);

        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_ATTACK_KNOCKBACK, 1f, 1f);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        // 디버그 로그
        Player p = e.getPlayer();
        manager.handlePlayerCollision(p);
        // 충돌 로그는 handlePlayerCollision 내부에 찍히도록 추가하세요
    }
}