package yd.kingdom.main.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import yd.kingdom.main.game.PositionManager;
import yd.kingdom.main.position.PositionType;

public class GoalkeeperCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        if (!(s instanceof Player p)) {
            s.sendMessage("플레이어만 사용 가능합니다.");
            return true;
        }
        PositionManager.getInstance().assign(p, PositionType.GOALKEEPER);
        return true;
    }
}