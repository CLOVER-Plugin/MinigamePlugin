package yd.kingdom.main.listener;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import yd.kingdom.main.Main;
import yd.kingdom.main.game.ItemManager;
import yd.kingdom.main.game.TeamManager;
import yd.kingdom.main.util.MessageUtil;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class BlockBreakDropListener implements Listener {

    private static final Set<Material> TARGET_BLOCKS = EnumSet.of(
            Material.OAK_PLANKS,
            Material.GREEN_WOOL,
            Material.WHITE_TERRACOTTA,
            Material.CLAY
    );

    private final ItemManager itemManager;
    private final TeamManager teamManager;

    public BlockBreakDropListener() {
        this.itemManager = Main.getInstance().getItemManager();
        this.teamManager = TeamManager.getInstance();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        // 해당 블럭이 아니면 무시
        if (!TARGET_BLOCKS.contains(e.getBlock().getType())) return;

        Player p = e.getPlayer();

        // 방해팀만 드랍 롤
        if (!teamManager.isAttackTeam(p)) return;

        final var block = e.getBlock();
        final Material originalType = block.getType();
        final BlockData originalData = block.getBlockData().clone();

        try { e.setDropItems(false); } catch (NoSuchMethodError ignore) {}

        // 1~100 주사위
        int roll = ThreadLocalRandom.current().nextInt(1, 101);

        ItemStack reward = null;
        String rewardName = null;

        if (roll <= 20) {
            reward = new ItemStack(Material.ARROW, 1);
            rewardName = "§7화살";
        } else if (roll <= 45) {
            reward = itemManager.getItemByKey("실명권").clone();
            rewardName = "§a그림팀 실명권";
        } else if (roll == 46) {
            reward = itemManager.getItemByKey("초기화권").clone();
            rewardName = "§a그림판 초기화권";
        } else if (roll <= 56) {
            reward = itemManager.getItemByKey("깽판권").clone();
            rewardName = "§b그림판 깽판권";
        } else if (roll <= 86) {
            reward = itemManager.getItemByKey("점프부스트").clone();
            rewardName = "§c점프력 100배";
        } else if (roll <= 96) {
            reward = itemManager.getItemByKey("좀비소환권").clone();
            rewardName = "§d좀비 10마리 소환권";
        } else { // 97~100
            reward = itemManager.getItemByKey("감옥권").clone();
            rewardName = "§e랜덤 감옥권";
        }

        if (reward != null) {
            Map<Integer, ItemStack> leftover = p.getInventory().addItem(reward);
            if (!leftover.isEmpty()) {
                ItemStack drop = leftover.values().iterator().next();
                // 블록 복원(2틱) 이후, 플레이어 위치에 드롭
                Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                    p.getWorld().dropItemNaturally(p.getLocation(), drop);
                }, 3L);
            }
            MessageUtil.send(p, "§a보상 획득: " + rewardName + " §7x1");
        }

        // 블럭 재생성
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            block.setType(originalType, false);
            block.setBlockData(originalData, false);
        }, 2L);
    }
}