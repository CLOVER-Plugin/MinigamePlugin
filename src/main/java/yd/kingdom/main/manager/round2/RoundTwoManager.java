package yd.kingdom.main.manager.round2;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import yd.kingdom.main.Main;
import yd.kingdom.main.game.TeamManager;

import java.util.HashSet;
import java.util.Set;

public class RoundTwoManager implements Listener {
    private final Main plugin = Main.getInstance();
    private final TeamManager tm = TeamManager.getInstance();
    private final Set<Player> pumpkined = new HashSet<>();
    private BukkitTask pumpkinCycleTask;
    private final Team hideNameTeam;
    private boolean active = false;

    public RoundTwoManager() {
        ScoreboardManager mgr = Bukkit.getScoreboardManager();
        Scoreboard board = mgr.getMainScoreboard();
        Team team = board.getTeam("pumpkin_hide");
        if (team == null) team = board.registerNewTeam("pumpkin_hide");
        team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        hideNameTeam = team;
    }

    public void begin() {
        if (active) return;
        active = true;

        Bukkit.getLogger().info("2라운드: 고깔축구 호박 로직 시작");

        // 즉시 호박 씌우기
        applyPumpkinToAll();

        // 2분 주기: 10초 호박 해제 → 다시 씌우기
        pumpkinCycleTask = new BukkitRunnable() {
            @Override
            public void run() {
                removePumpkinFromAll();
                // 10초 뒤 다시 착용
                new BukkitRunnable() {
                    @Override public void run() {
                        if (active) applyPumpkinToAll();
                    }
                }.runTaskLater(plugin, 20 * 10);
            }
        }.runTaskTimer(plugin, 20 * 120, 20 * 120);
    }

    public void teleportAllToArena() {
        Bukkit.getLogger().info("2라운드 진입: 전원 경기장 TP");
        Location tpLocation = new Location(Bukkit.getWorld("world"), -94, -60, -149);
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getGameMode() != GameMode.SPECTATOR) {
                player.teleport(tpLocation);
            }
        }
    }

    public void stop() {
        if (pumpkinCycleTask != null) {
            pumpkinCycleTask.cancel();
            pumpkinCycleTask = null;
        }
        removePumpkinFromAll();
        active = false;
    }

    private void applyPumpkinToAll() {
        pumpkined.clear();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if ((tm.isAttackTeam(p) || tm.isDefendTeam(p)) && p.getGameMode() != GameMode.SPECTATOR) {
                p.getInventory().setHelmet(new ItemStack(Material.CARVED_PUMPKIN));
                p.updateInventory();
                pumpkined.add(p);
                hideNameTeam.addEntry(p.getName());
            }
        }
    }

    private void removePumpkinFromAll() {
        for (Player p : new HashSet<>(pumpkined)) {
            p.getInventory().setHelmet(null);
            p.updateInventory();
            hideNameTeam.removeEntry(p.getName());
        }
        pumpkined.clear();
    }

    // 라운드2엔 서로 타격 금지
    @EventHandler
    public void onPvP(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player d) || !(e.getEntity()   instanceof Player v)) return;
        if (Main.getInstance().getGameManager().getCurrentRound() == 2
                && ((tm.isAttackTeam(d)||tm.isDefendTeam(d))
                && (tm.isAttackTeam(v)||tm.isDefendTeam(v)))) {
            e.setCancelled(true);
        }
    }

    // 호박 제거 금지
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;
        if (!pumpkined.contains(p)) return;
        if (e.getSlotType() == InventoryType.SlotType.ARMOR && e.getSlot() == 3) {
            e.setCancelled(true);
        }
    }
}