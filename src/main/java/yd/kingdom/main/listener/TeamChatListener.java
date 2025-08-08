package yd.kingdom.main.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import yd.kingdom.main.game.TeamManager;

public class TeamChatListener implements Listener {
    private final TeamManager tm = TeamManager.getInstance();

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        // 1) 모드 진입 여부 미리 저장 (이 메시지가 end이건 아니건 취소용)
        boolean wasSetting = tm.isSetting();

        // 2) 설정 로직 처리 (end면 모드 종료)
        tm.handleChatInput(e.getPlayer(), e.getMessage());

        // 3) 모드 중이었으면(=end 처리 전에도 true) 채팅 전파 금지
        if (wasSetting) {
            e.setCancelled(true);
        }
    }
}