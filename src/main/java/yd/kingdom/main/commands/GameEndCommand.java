package yd.kingdom.main.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import yd.kingdom.main.Main;
import yd.kingdom.main.game.GameManager;
import yd.kingdom.main.util.MessageUtil;

public class GameEndCommand implements CommandExecutor {
    private final GameManager gm = Main.getInstance().getGameManager();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        gm.endGame();
        MessageUtil.send(sender, "게임을 종료하고 초기화했습니다.");
        return true;
    }
}