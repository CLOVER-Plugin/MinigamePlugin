package yd.kingdom.main.listener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import yd.kingdom.main.Main;
import yd.kingdom.main.game.GameManager;
import yd.kingdom.main.game.ItemManager;
import yd.kingdom.main.game.TeamManager;
import yd.kingdom.main.util.LocationUtil;
import yd.kingdom.main.util.MessageUtil;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class ItemListener implements Listener {
    private final ItemManager itemManager = Main.getInstance().getItemManager();
    private final TeamManager teamManager = TeamManager.getInstance();
    private final GameManager gameManager = Main.getInstance().getGameManager();
    private static final Map<UUID, ItemStack[]> savedInventories = new ConcurrentHashMap<>();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (!teamManager.isAttackTeam(player)) return;
        Material broken = event.getBlock().getType();
        ItemStack reward = itemManager.getRandomAttackItem(broken);
        if (reward != null) player.getInventory().addItem(reward);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null || !item.hasItemMeta()) return;
        Player attacker = event.getPlayer();
        String name = item.getItemMeta().getDisplayName();
        switch (name) {
            case "§a그림팀 그림판 초기화권":
                consumeItem(attacker, item);
                LocationUtil.clearArea(
                        LocationUtil.getCanvasCorner1(),
                        LocationUtil.getCanvasCorner2()
                );
                MessageUtil.send(attacker, "그림판 영역이 초기화되었습니다.");
                break;
            case "§b그림팀 그림판 깽판권":
                // 방해팀만 사용
                if (!teamManager.isAttackTeam(attacker)) {
                    MessageUtil.send(attacker, "§c방해팀만 사용할 수 있습니다.");
                    event.setCancelled(true);
                    return;
                }
                consumeItem(attacker, item);
                UUID id = attacker.getUniqueId();
                // 1) 인벤 저장 후 클리어
                savedInventories.put(id, attacker.getInventory().getContents());
                attacker.getInventory().clear();
                attacker.updateInventory();

                // 2) 워프
                Location prev = attacker.getLocation();
                attacker.teleport(LocationUtil.getZombieSpawnLocation());
                MessageUtil.send(attacker, "§a그림판으로 워프되었습니다! 10초 후 이전 위치로 복귀합니다.");

                // 3) 10초 뒤 원위치 & 인벤 복원
                new BukkitRunnable() {
                    @Override public void run() {
                        attacker.teleport(prev);
                        ItemStack[] saved = savedInventories.remove(id);
                        if (saved != null) {
                            attacker.getInventory().setContents(saved);
                            attacker.updateInventory();
                        }
                        MessageUtil.send(attacker, "§a이전 위치로 복귀되었습니다. 인벤토리가 복원되었습니다.");
                    }
                }.runTaskLater(Bukkit.getPluginManager().getPlugin("MinigamePlugin"), 20 * 10);

                event.setCancelled(true);
                break;
            case "§c점프력 100배":
                consumeItem(attacker, item);
                for (Player dp : teamManager.getDefendTeam()) {
                    dp.addPotionEffect(
                            new PotionEffect(PotionEffectType.JUMP_BOOST, 200, 100)
                    );
                }
                MessageUtil.send(attacker, "그림팀에게 점프 부스트를 적용했습니다.");
                break;
            case "§d좀비 10마리 소환권":
                consumeItem(attacker, item);
                Location spawn = LocationUtil.getZombieSpawnLocation();
                for (int i = 0; i < 10; i++) {
                    spawn.getWorld().spawnEntity(spawn, EntityType.ZOMBIE);
                }
                MessageUtil.send(attacker, "좀비 10마리를 소환했습니다.");
                break;
            case "§e랜덤 감옥권":
                consumeItem(attacker, item);
                Set<Player> defs = teamManager.getDefendTeam();
                if (defs.isEmpty()) return;
                int idx = ThreadLocalRandom.current().nextInt(defs.size());
                Player target = defs.stream().skip(idx).findFirst().orElse(null);
                if (target == null) return;
                Location jail = LocationUtil.getJailLocation();
                Location release = LocationUtil.getReleaseLocation();
                target.teleport(jail);
                MessageUtil.send(attacker, target.getName() + "님을 감옥에 보냈습니다.");
                new BukkitRunnable() {
                    @Override public void run() {
                        target.teleport(release);
                        MessageUtil.send(target, "감옥에서 풀려났습니다.");
                    }
                }.runTaskLater(Bukkit.getPluginManager()
                        .getPlugin("MinigamePlugin"), 300L);
                break;
            default:
                return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().contains("교환 NPC")) return;
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        int slot = event.getRawSlot();
        itemManager.exchangeItem(player, slot);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Arrow arrow)) return;
        if (!(arrow.getShooter() instanceof Player attacker)) return;
        if (!teamManager.isAttackTeam(attacker)) return;
        if (!(event.getEntity() instanceof Player victim)) return;
        if (!teamManager.isDefendTeam(victim)) return;
        victim.addPotionEffect(
                new PotionEffect(PotionEffectType.BLINDNESS, 100, 1)
        );
        MessageUtil.send(attacker, victim.getName() + "님에게 실명 효과를 적용했습니다.");
    }

    private void consumeItem(Player p, ItemStack item) {
        if (item.getAmount() > 1) {
            item.setAmount(item.getAmount() - 1);
        } else {
            p.getInventory().remove(item);
        }
        p.updateInventory();
    }
}