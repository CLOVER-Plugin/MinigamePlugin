package yd.kingdom.main.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import yd.kingdom.main.Main;
import yd.kingdom.main.game.GameManager;
import yd.kingdom.main.manager.round2.RoundTwoManager;
import yd.kingdom.main.util.MessageUtil;

public class GameStartCommand implements CommandExecutor {
    private final GameManager gm = Main.getInstance().getGameManager();
    private final RoundTwoManager r2 = Main.getInstance().getRoundTwoManager();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        gm.startNextRound();
        MessageUtil.send(sender, "게임을 시작합니다. 현재 라운드: " + gm.getCurrentRound());

        // 라운드가 2가 되면 TP만 수행 (호박 로직은 /라운드2시작에서 따로 시작)
        if (gm.getCurrentRound() == 2) {
            r2.teleportAllToArena();
        }
        return true;
    }
}