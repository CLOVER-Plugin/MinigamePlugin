package yd.kingdom.main.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import yd.kingdom.main.game.SoccerBallManager;
import yd.kingdom.main.util.MessageUtil;

public class SoccerBallCommand implements CommandExecutor {
    private final SoccerBallManager manager = SoccerBallManager.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("플레이어만 사용할 수 있습니다.");
            return true;
        }
        Player player = (Player) sender;
        if (args.length != 1) {
            MessageUtil.send(player, "사용법: /축구공 <소환|해제>");
            return false;
        }
        switch (args[0].toLowerCase()) {
            case "소환":
                manager.spawnBall(player);
                break;
            case "해제":
                manager.clearBalls();
                MessageUtil.send(player, "모든 축구공을 제거했습니다.");
                break;
            default:
                MessageUtil.send(player, "사용법: /축구공 <소환|해제>");
                return false;
        }
        return true;
    }
}