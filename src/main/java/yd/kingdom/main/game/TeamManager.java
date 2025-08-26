package yd.kingdom.main.game;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import yd.kingdom.main.util.MessageUtil;

import java.util.*;

public class TeamManager {
    private static final TeamManager instance = new TeamManager();
    private final Set<Player> teamA = new HashSet<>();
    private final Set<Player> teamB = new HashSet<>();
    private final Set<Character> ropeEnabled = new HashSet<>();

    private char attackSide = 0;
    private char currentSetupTeam = 0;
    private CommandSender setupSender;

    private TeamManager() {}
    public static TeamManager getInstance() { return instance; }

    public void startSetup(String team, CommandSender sender) {
        if (team == null || team.isEmpty()) return;
        currentSetupTeam = team.charAt(0);
        setupSender = sender;
        MessageUtil.send(sender, currentSetupTeam + "팀 설정 모드에 들어갔습니다. 'end'를 입력하면 종료됩니다.");
    }

    public void handleChatInput(Player chattingPlayer, String message) {
        if (currentSetupTeam == 0) return;
        if (!(setupSender instanceof Player) || !((Player) setupSender).equals(chattingPlayer)) return;
        String msg = message.trim();
        if (msg.equalsIgnoreCase("end")) {
            MessageUtil.send(setupSender, "팀 설정 모드가 종료되었습니다.");
            currentSetupTeam = 0;
            setupSender = null;
            return;
        }
        Player target = chattingPlayer.getServer().getPlayerExact(msg);
        if (target == null) {
            MessageUtil.send(setupSender, "플레이어를 찾을 수 없습니다: " + msg);
            return;
        }
        if (currentSetupTeam == 'A') {
            teamB.remove(target);
            teamA.add(target);
            TeamColorManager.getInstance().assign(target, 'A');
            MessageUtil.send(setupSender, target.getName() + "님을 A팀에 추가했습니다.");
        } else {
            teamA.remove(target);
            teamB.add(target);
            TeamColorManager.getInstance().assign(target, 'B');
            MessageUtil.send(setupSender, target.getName() + "님을 B팀에 추가했습니다.");
        }
    }



    public void playerTeam(Player chattingPlayer, String message) {
        if (currentSetupTeam == 0) return;
        if (!(setupSender instanceof Player) || !((Player) setupSender).equals(chattingPlayer)) return;
        String msg = message.trim();
        Player target = chattingPlayer.getServer().getPlayerExact(msg);
        if (target == null) {
            MessageUtil.send(setupSender, "플레이어를 찾을 수 없습니다: " + msg);
            return;
        }
        if (msg == "A") {
            teamB.remove(target);
            teamA.add(target);
            TeamColorManager.getInstance().assign(target, 'A');
            MessageUtil.send(setupSender, target.getName() + "님을 A팀에 추가했습니다.");
        } else {
            teamA.remove(target);
            teamB.add(target);
            TeamColorManager.getInstance().assign(target, 'B');
            MessageUtil.send(setupSender, target.getName() + "님을 B팀에 추가했습니다.");
        }
    }


    public boolean isSetting() {
        return currentSetupTeam != 0;
    }

    public void setAttackTeam(String team) {
        if (team == null || team.isEmpty()) return;
        attackSide = team.charAt(0);
    }

    public void setDefendTeam(String team) {
        if (team == null || team.isEmpty()) return;
        attackSide = (team.charAt(0) == 'A') ? 'B' : 'A';
    }

    public boolean isAttackTeam(Player player) {
        return attackSide == 'A' ? teamA.contains(player) : teamB.contains(player);
    }

    public boolean isDefendTeam(Player player) {
        return attackSide == 'A' ? teamB.contains(player) : teamA.contains(player);
    }

    public Set<Player> getAttackTeam() { return attackSide == 'A' ? teamA : teamB; }
    public Set<Player> getDefendTeam() { return attackSide == 'A' ? teamB : teamA; }

    public boolean toggleRope(String team) {
        if (team == null || team.isEmpty()) return false;
        char t = team.charAt(0);
        Set<Player> players = (t == 'A') ? teamA : teamB;
        boolean enabled;
        if (ropeEnabled.contains(t)) {
            ropeEnabled.remove(t);
            RopeManager.getInstance().stopRope();
            enabled = false;
        } else {
            ropeEnabled.add(t);
            RopeManager.getInstance().startRope(players);
            enabled = true;
        }
        return enabled;
    }

    public boolean isRopeEnabled(char team) {
        return ropeEnabled.contains((char)team);
    }
}