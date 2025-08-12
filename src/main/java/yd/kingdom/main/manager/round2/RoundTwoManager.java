package yd.kingdom.main.manager.round2;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEvent;
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
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;

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
                p.getInventory().setHelmet(boundPumpkin());
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
    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;
        if (!pumpkined.contains(p)) return;

        // 현재 헬멧이 호박인지 확인
        ItemStack helmet = p.getInventory().getHelmet();
        if (!isPumpkin(helmet)) return;

        // 1) 헬멧 슬롯 직접 클릭/교체/버리기 시도 막기
        if (e.getSlotType() == InventoryType.SlotType.ARMOR) {
            ItemStack clicked = e.getCurrentItem();
            // 헬멧 슬롯이면 clicked가 호박일 것. 커서로 다른 헬멧 올려 교체도 여기서 걸림
            if (isPumpkin(clicked) || isHeadEquip(e.getCursor())) {
                e.setCancelled(true);
                return;
            }
        }

        // 2) 쉬프트클릭으로 헬멧 장착 시도 막기
        if ((e.getClick() == ClickType.SHIFT_LEFT || e.getClick() == ClickType.SHIFT_RIGHT)
                && isHeadEquip(e.getCurrentItem())) {
            e.setCancelled(true);
            return;
        }

        // 3) 숫자키로 핫바-헬멧 슬롯 스왑 막기
        if (e.getClick() == ClickType.NUMBER_KEY) {
            ItemStack hotbarItem = p.getInventory().getItem(e.getHotbarButton());
            if (isHeadEquip(hotbarItem) && e.getSlotType() == InventoryType.SlotType.ARMOR) {
                e.setCancelled(true);
                return;
            }
        }

        // 4) 기타 액션으로 헬멧에 들어가려는 경우(안전망)
        if (e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY && isHeadEquip(e.getCurrentItem())) {
            e.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryDrag(InventoryDragEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;
        if (!pumpkined.contains(p)) return;

        ItemStack helmet = p.getInventory().getHelmet();
        if (!isPumpkin(helmet)) return;

        // 드래그가 ARMOR 슬롯(특히 헬멧 슬롯)을 건드리면 취소
        for (int raw : e.getRawSlots()) {
            if (e.getView().getSlotType(raw) == InventoryType.SlotType.ARMOR) {
                ItemStack inSlot = e.getView().getItem(raw);
                if (isPumpkin(inSlot) || isHeadEquip(e.getOldCursor())) {
                    e.setCancelled(true);
                    return;
                }
            }
        }
    }

    // 인벤토리 밖에서 우클릭 장착(공중/블록 우클릭)으로 헬멧 교체 시도 막기
    @EventHandler(ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (!pumpkined.contains(p)) return;

        ItemStack helmet = p.getInventory().getHelmet();
        if (!isPumpkin(helmet)) return;

        Action a = e.getAction();
        if (a == Action.RIGHT_CLICK_AIR || a == Action.RIGHT_CLICK_BLOCK) {
            ItemStack hand = e.getItem();
            if (isHeadEquip(hand)) {
                e.setCancelled(true);               // 장착 자체 차단
                e.setUseItemInHand(Event.Result.DENY);
            }
        }
    }

    // ===== 유틸 =====
    private boolean isPumpkin(ItemStack it) {
        if (it == null) return false;
        Material t = it.getType();
        return t == Material.CARVED_PUMPKIN || t == Material.PUMPKIN; // 서버 버전에 따라 둘 다 체크
    }

    private boolean isHeadEquip(ItemStack it) {
        if (it == null) return false;
        Material m = it.getType();
        // 헬멧류 + 호박 + 머리류 전부(우클릭 장착 가능)
        return m.name().endsWith("_HELMET")
                || m == Material.TURTLE_HELMET
                || m == Material.CARVED_PUMPKIN
                || m == Material.PUMPKIN
                || m.name().endsWith("_HEAD")
                || m.name().endsWith("_SKULL");
    }

    private ItemStack boundPumpkin() {
        ItemStack it = new ItemStack(Material.CARVED_PUMPKIN);
        // 결속의 저주 부여 (unsafe 사용해도 1레벨만 붙음)
        it.addUnsafeEnchantment(Enchantment.BINDING_CURSE, 1);
        it.editMeta(meta -> {
            meta.setUnbreakable(true);                 // 선택: 내구도 숨기기용
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS); // 인챈트 이펙트 숨김
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        });
        return it;
    }
}