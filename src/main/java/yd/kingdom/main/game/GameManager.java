package yd.kingdom.main.game;

import org.bukkit.Bukkit;
import yd.kingdom.main.Main;
import yd.kingdom.main.manager.round1.RoundOneManager;
import yd.kingdom.main.manager.round2.RoundTwoManager;
import yd.kingdom.main.manager.round3.RoundThreeManager;

public class GameManager {
    private final RoundOneManager roundOne;
    private final RoundTwoManager roundTwo;
    private final RoundThreeManager roundThree;
    private int currentRound = 0;

    public GameManager(Main plugin) {
        this.roundOne = new RoundOneManager(plugin);
        this.roundTwo = Main.getInstance().getRoundTwoManager();
        this.roundThree = new RoundThreeManager(plugin);
    }

    public void startNextRound() {
        currentRound++;
        switch (currentRound) {
            case 1 -> roundOne.start();
            case 2 -> roundTwo.start();
            case 3 -> roundThree.start();
            default -> Bukkit.getLogger().info("모든 라운드 완료");
        }
    }

    public void endGame() {
        Bukkit.getLogger().info("게임 종료 및 상태 초기화");
        currentRound = 0;
    }

    /**
     * 현재 라운드 번호를 반환합니다.
     */
    public int getCurrentRound() {
        return currentRound;
    }
}
