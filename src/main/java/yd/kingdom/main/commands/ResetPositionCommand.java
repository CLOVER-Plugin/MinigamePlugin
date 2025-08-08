package yd.kingdom.main.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import yd.kingdom.main.game.PositionManager;
import yd.kingdom.main.util.MessageUtil;

public class ResetPositionCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage("플레이어만 사용할 수 있음");
            return true;
        }

        PositionManager.getInstance().clear(p);
        MessageUtil.send(p, "포지션이 초기화되었습니다!");
        return true;
    }

}
