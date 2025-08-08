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

        return true;
    }
}