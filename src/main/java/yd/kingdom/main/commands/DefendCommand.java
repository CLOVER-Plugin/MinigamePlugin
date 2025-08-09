package yd.kingdom.main.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import yd.kingdom.main.game.TeamManager;
import yd.kingdom.main.util.MessageUtil;

public class DefendCommand implements CommandExecutor {
    private final TeamManager tm = TeamManager.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length != 1) {
            MessageUtil.send(sender, "사용법: /그림 <A | B>");
            return false;
        }
        String team = args[0].toUpperCase();
        tm.setDefendTeam(team);
        MessageUtil.send(sender, team + "팀을 그림팀으로 설정했습니다.");

        for (Player p : tm.getDefendTeam()) {
            p.getInventory().clear();
            p.updateInventory();
        }
        return true;
    }
}