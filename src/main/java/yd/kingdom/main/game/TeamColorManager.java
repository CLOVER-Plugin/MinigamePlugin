package yd.kingdom.main.game;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class TeamColorManager {
    private static final TeamColorManager instance = new TeamColorManager();

    private final Scoreboard board;
    private final Team teamA;
    private final Team teamB;

    private TeamColorManager() {
        this.board = Bukkit.getScoreboardManager().getMainScoreboard();

        Team a = board.getTeam("A");
        if (a == null) a = board.registerNewTeam("A");
        a.setColor(ChatColor.RED);
        a.setCanSeeFriendlyInvisibles(true);
        a.setAllowFriendlyFire(true);
        this.teamA = a;

        Team b = board.getTeam("B");
        if (b == null) b = board.registerNewTeam("B");
        b.setColor(ChatColor.BLUE);
        b.setCanSeeFriendlyInvisibles(true);
        b.setAllowFriendlyFire(true);
        this.teamB = b;
    }

    public static TeamColorManager getInstance() {
        return instance;
    }

    /** 플레이어를 A/B 팀 색으로 지정 (반대 팀에 있으면 제거) */
    public void assign(Player p, char team) {
        // 먼저 양쪽 팀에서 제거
        teamA.removeEntry(p.getName());
        teamB.removeEntry(p.getName());

        if (team == 'A') {
            teamA.addEntry(p.getName());
        } else if (team == 'B') {
            teamB.addEntry(p.getName());
        }
    }

    /** 팀 색 초기화 */
    public void clear(Player p) {
        teamA.removeEntry(p.getName());
        teamB.removeEntry(p.getName());
    }
}