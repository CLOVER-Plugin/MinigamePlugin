package yd.kingdom.main.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import yd.kingdom.main.game.TeamManager;
import yd.kingdom.main.util.MessageUtil;

public class TeamSetupCommand implements CommandExecutor {
    private final TeamManager teamManager = TeamManager.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length < 2) {
            MessageUtil.send(sender, "/팀 설정 <A | B> : A|B 팀 플레이어 설정");
            MessageUtil.send(sender, "/팀 끈 <A | B> : 설정된 A|B 팀 플레이어들끼리 끈 연결/해제");
            return false;
        }

        String sub = args[0];
        String team = args[1].toUpperCase();
        String targetName = args[2];

        switch (sub) {
            case "설정":
                MessageUtil.send(sender, team + "팀 설정 모드입니다. 플레이어를 채팅으로 입력하세요.");
                teamManager.startSetup(team, sender);
                break;

            case "끈":
                boolean enabled = teamManager.toggleRope(team);
                if (enabled) {
                    MessageUtil.send(sender, team + "팀의 플레이어들이 줄로 연결되었습니다.");
                } else {
                    MessageUtil.send(sender, team + "팀의 줄 연결이 해제되었습니다.");
                }
                break;
            case "강제":
                Player target = Bukkit.getPlayerExact(targetName);
                teamManager.playerTeam(target, team);
                MessageUtil.send(sender, target+"님이 "+team+"팀으로 배정되었습니다.");
                break;

            default:
                MessageUtil.send(sender, "알 수 없는 서브커맨드입니다.");
        }
        return true;
    }
}