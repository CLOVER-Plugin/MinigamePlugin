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
        itemMap.put("실명권", createNamedItem(Material.ENDER_EYE, "§a그림팀 실명권"));
        itemMap.put("초기화권", createNamedItem(Material.BARRIER, "§a그림팀 그림판 초기화권"));
        itemMap.put("깽판권", createNamedItem(Material.SLIME_BALL, "§b그림팀 그림판 깽판권"));
        itemMap.put("점프부스트", createNamedItem(Material.FEATHER, "§c점프력 100배"));
        itemMap.put("좀비소환권", createNamedItem(Material.ROTTEN_FLESH, "§d좀비 2마리 소환권"));
        itemMap.put("감옥권", createNamedItem(Material.SNOWBALL, "§e랜덤 감옥권"));
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
}