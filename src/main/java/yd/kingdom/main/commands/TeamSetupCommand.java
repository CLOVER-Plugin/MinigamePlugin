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
        if (args.length == 0) {
            printHelp(sender);
            return true;
        }

        String sub = args[0];

        switch (sub) {
            case "설정": {
                // /팀 설정 <A|B>
                if (args.length < 2) {
                    MessageUtil.send(sender, "사용법: /팀 설정 <A|B>");
                    return true;
                }
                String team = args[1].toUpperCase();
                if (!isValidTeam(team)) {
                    MessageUtil.send(sender, "팀은 A 또는 B만 가능합니다.");
                    return true;
                }
                MessageUtil.send(sender, team + "팀 설정 모드입니다. 플레이어를 채팅으로 입력하세요.");
                teamManager.startSetup(team, sender);
                return true;
            }

            case "끈": {
                // /팀 끈 <A|B>
                if (args.length < 2) {
                    MessageUtil.send(sender, "사용법: /팀 끈 <A|B>");
                    return true;
                }
                String team = args[1].toUpperCase();
                if (!isValidTeam(team)) {
                    MessageUtil.send(sender, "팀은 A 또는 B만 가능합니다.");
                    return true;
                }
                boolean enabled = teamManager.toggleRope(team);
                if (enabled) {
                    MessageUtil.send(sender, team + "팀의 플레이어들이 줄로 연결되었습니다.");
                } else {
                    MessageUtil.send(sender, team + "팀의 줄 연결이 해제되었습니다.");
                }
                return true;
            }

            case "강제": {
                // /팀 강제 <A|B> <playername>
                if (args.length < 3) {
                    MessageUtil.send(sender, "사용법: /팀 강제 <A|B> <플레이어이름>");
                    return true;
                }
                String team = args[1].toUpperCase();
                if (!isValidTeam(team)) {
                    MessageUtil.send(sender, "팀은 A 또는 B만 가능합니다.");
                    return true;
                }
                String targetName = args[2];
                Player target = Bukkit.getPlayerExact(targetName);
                if (target == null) {
                    MessageUtil.send(sender, "해당 플레이어를 찾을 수 없습니다. (온라인 여부 확인)");
                    return true;
                }
                teamManager.playerTeam(target, team);
                MessageUtil.send(sender, target.getName() + "님이 " + team + "팀으로 배정되었습니다.");
                return true;
            }

            default:
                MessageUtil.send(sender, "알 수 없는 서브커맨드입니다.");
                printHelp(sender);
                return true;
        }
    }

    private boolean isValidTeam(String team) {
        return "A".equalsIgnoreCase(team) || "B".equalsIgnoreCase(team);
    }

    private void printHelp(CommandSender sender) {
        MessageUtil.send(sender, "/팀 설정 <A | B> : A|B 팀 플레이어 설정");
        MessageUtil.send(sender, "/팀 끈 <A | B> : 설정된 A|B 팀 플레이어들끼리 끈 연결/해제");
        MessageUtil.send(sender, "/팀 강제 <A | B> <player> : 플레이어를 A|B 팀으로 강제 배정");
    }
}
