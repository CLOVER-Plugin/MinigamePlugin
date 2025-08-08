package yd.kingdom.main.game;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import yd.kingdom.main.Main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UnlimitedChestManager implements Listener {

    private final Main plugin;
    private final List<ChestConfig> chests = new ArrayList<>();
    private static final String GUI_TITLE = "§6무제한 상자";

    public UnlimitedChestManager(Main plugin) {
        this.plugin = plugin;
        loadConfig();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @SuppressWarnings("unchecked")
    private void loadConfig() {
        List<?> rawList = plugin.getConfig().getList("unlimited-chests");
        if (rawList == null || rawList.isEmpty()) {
            plugin.getLogger().warning("unlimited-chests 설정이 없습니다.");
            return;
        }
        for (Object obj : rawList) {
            if (!(obj instanceof Map<?, ?> entryMap)) continue;
            Map<?, ?> entry = entryMap;

            Object worldObj = entry.get("world");
            Object xo = entry.get("x"), yo = entry.get("y"), zo = entry.get("z");
            if (!(worldObj instanceof String) || xo == null || yo == null || zo == null) continue;
            String world = (String) worldObj;
            double x = ((Number) xo).doubleValue();
            double y = ((Number) yo).doubleValue();
            double z = ((Number) zo).doubleValue();
            if (Bukkit.getWorld(world) == null) {
                plugin.getLogger().warning("존재하지 않는 월드: " + world);
                continue;
            }
            Location loc = new Location(Bukkit.getWorld(world), x, y, z);

            Map<Integer, ItemStack> slotItems = new HashMap<>();
            Object slotsRaw = entry.get("slots");
            if (slotsRaw instanceof Map<?, ?> slotsRawMap) {
                for (Map.Entry<?, ?> slotEntry : slotsRawMap.entrySet()) {
                    int slot;
                    Object rawKey = slotEntry.getKey();
                    if (rawKey instanceof Number) slot = ((Number) rawKey).intValue();
                    else try { slot = Integer.parseInt(rawKey.toString()); } catch (Exception ex) { continue; }

                    Object valObj = slotEntry.getValue();
                    if (!(valObj instanceof Map<?, ?> valMapRaw)) continue;
                    Map<?, ?> valMap = valMapRaw;
                    Object matObj = valMap.get("material");
                    Object amtObj = valMap.get("amount");
                    if (!(matObj instanceof String)) continue;
                    String matName = ((String) matObj).toUpperCase();
                    int amount = (amtObj instanceof Number) ? ((Number) amtObj).intValue() : 1;
                    Material mat = Material.getMaterial(matName);
                    if (mat != null) {
                        slotItems.put(slot, new ItemStack(mat, amount));
                    }
                }
            }
            chests.add(new ChestConfig(loc, slotItems));
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block block = e.getClickedBlock();
        if (block == null) return;
        Location clicked = block.getLocation();
        for (ChestConfig cfg : chests) {
            if (isSameBlock(clicked, cfg.loc)) {
                // 열림 효과: 소리 + 파티클
                clicked.getWorld().playSound(clicked, Sound.BLOCK_CHEST_OPEN, 1.0f, 1.0f);
                clicked.getWorld().spawnParticle(Particle.WHITE_SMOKE, clicked.clone().add(0.5, 0.5, 0.5), 10, 0.3, 0.3, 0.3);
                openGui(e, cfg);
                e.setCancelled(true);
                break;
            }
        }
    }

    private void openGui(PlayerInteractEvent e, ChestConfig cfg) {
        Player player = e.getPlayer();
        int size = 54;
        Inventory gui = Bukkit.createInventory(null, size, GUI_TITLE);
        cfg.slotItems.forEach(gui::setItem);
        player.openInventory(gui);
    }

    private boolean isSameBlock(Location a, Location b) {
        return a.getWorld().equals(b.getWorld())
                && a.getBlockX() == b.getBlockX()
                && a.getBlockY() == b.getBlockY()
                && a.getBlockZ() == b.getBlockZ();
    }

    private static class ChestConfig {
        final Location loc;
        final Map<Integer, ItemStack> slotItems;
        ChestConfig(Location loc, Map<Integer, ItemStack> slotItems) {
            this.loc = loc;
            this.slotItems = slotItems;
        }
    }
}
