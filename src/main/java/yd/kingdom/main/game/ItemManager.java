package yd.kingdom.main.game;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import yd.kingdom.main.Main;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ItemManager {
    private final Main plugin;
    private final Map<String, ItemStack> itemMap = new HashMap<>();
    private final Random random = new Random();

    public ItemManager(Main plugin) {
        this.plugin = plugin;
        registerItems();
    }

    private void registerItems() {
        // display name and key should correspond to ItemCommand keys
        itemMap.put("초기화권", createNamedItem(Material.BARRIER, "§a수비팀 그림판 초기화권"));
        itemMap.put("깽판권", createNamedItem(Material.SLIME_BALL, "§b수비팀 그림판 깽판권"));
        itemMap.put("점프부스트", createNamedItem(Material.FEATHER, "§c점프력 100배"));
        itemMap.put("좀비소환권", createNamedItem(Material.ROTTEN_FLESH, "§d좀비 10마리 소환권"));
        itemMap.put("감옥권", createNamedItem(Material.LEGACY_IRON_FENCE, "§e랜덤 감옥권"));
    }

    private ItemStack createNamedItem(Material mat, String displayName) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack getItemByKey(String key) {
        return itemMap.get(key);
    }

    public ItemStack getRandomAttackItem(Material broken) {
        // Simple random: choose any registered item
        Object[] keys = itemMap.values().toArray();
        return (ItemStack) keys[random.nextInt(keys.length)];
    }

    public void exchangeItem(org.bukkit.entity.Player player, int slot) {
        // Implement NPC exchange logic based on slot index
        // Placeholder: give a pass
        player.sendMessage("아이템 교환 기능은 아직 구현되지 않았습니다.");
    }
}