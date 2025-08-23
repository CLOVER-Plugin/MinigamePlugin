package yd.kingdom.main.game;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import yd.kingdom.main.position.PositionType;
import yd.kingdom.main.util.MessageUtil;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PositionManager {
    private static final PositionManager instance = new PositionManager();
    private final Map<UUID, PositionType> positions = new HashMap<>();
    private final Map<UUID, Map<PositionType, Long>> lastUse = new HashMap<>();
    private static final long COOLDOWN = 30 * 1000; // 30초

    public static PositionManager getInstance() { return instance; }

    /** 포지션 설정 시 기존 아이템 제거 후 새 아이템 지급 */
    public void assign(Player p, PositionType type) {
        positions.put(p.getUniqueId(), type);
        removeAllPositionItems(p);
        giveItem(p, type);
        MessageUtil.send(p, type.name() + " 포지션이 설정되었습니다.");
    }

    public PositionType get(Player p) {
        return positions.get(p.getUniqueId());
    }

    private void giveItem(Player p, PositionType type) {
        ItemStack item = null;
        switch (type) {
            case GOALKEEPER:
                item = new ItemStack(Material.CLAY_BALL);
                ItemMeta gm = item.getItemMeta();
                gm.setDisplayName("§6골키퍼 장갑");
                item.setItemMeta(gm);
                break;

            case DEFENDER:
                item = new ItemStack(Material.STICK);
                ItemMeta dm = item.getItemMeta();
                dm.setDisplayName("§b수비수 막대기");
                item.setItemMeta(dm);
                break;

            case ATTACKER:
                item = new ItemStack(Material.DIAMOND_BOOTS);
                ItemMeta am = item.getItemMeta();
                am.setDisplayName("§c공격수 신발");
                item.setItemMeta(am);
                break;

            default:
                return;
        }

        if (item != null) {
            p.getInventory().addItem(item);
        }
    }

    private void removeAllPositionItems(Player p) {
        PlayerInventory inv = p.getInventory();
        for (int slot = 0; slot < inv.getSize(); slot++) {
            ItemStack item = inv.getItem(slot);
            if (item == null || !item.hasItemMeta()) continue;
            String name = item.getItemMeta().getDisplayName();
            if (name.equals("§6골키퍼 장갑")
                    || name.equals("§b수비수 막대기")
                    || name.equals("§c공격수 신발")) {
                inv.setItem(slot, null);
            }
        }
        p.updateInventory();
    }

    public boolean tryUse(Player p) {
        PositionType type = get(p);
        if (type == null) return false;
        long now = System.currentTimeMillis();
        long last = lastUse
                .computeIfAbsent(p.getUniqueId(), u -> new EnumMap<>(PositionType.class))
                .getOrDefault(type, 0L);
        if (now - last < COOLDOWN) {
            long rem = (COOLDOWN - (now - last)) / 1000;
            p.spigot().sendMessage(
                    net.md_5.bungee.api.ChatMessageType.ACTION_BAR,
                    new net.md_5.bungee.api.chat.TextComponent(type.name() + " 스킬 쿨타임: " + rem + "초")
            );
            return false;
        }
        lastUse.get(p.getUniqueId()).put(type, now);
        return true;
    }

    public void clear(Player p) {
        positions.remove(p.getUniqueId());
        removeAllPositionItems(p);
        // 쿨타임 기록도 삭제
        lastUse.remove(p.getUniqueId());
    }
}