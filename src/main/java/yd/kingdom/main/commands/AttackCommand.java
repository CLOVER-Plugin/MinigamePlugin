package yd.kingdom.main.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import yd.kingdom.main.game.TeamManager;
import yd.kingdom.main.util.MessageUtil;

public class AttackCommand implements CommandExecutor {
    private final TeamManager tm = TeamManager.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length != 1) {
            MessageUtil.send(sender, "사용법: /공격 <A|B>");
            return false;
        }
        String team = args[0].toUpperCase();
        tm.setAttackTeam(team);
        MessageUtil.send(sender, team + "팀을 공격으로 설정했습니다.");

        for (Player p : tm.getAttackTeam()) {
            p.getInventory().clear();
            p.updateInventory();
            giveAttackTools(p);
            MessageUtil.send(p, "§a[오합지졸] 공격팀 툴이 지급되었습니다.");
        }

        return true;
    }

    private void giveAttackTools(Player p) {
        Material[] tools = {
                Material.DIAMOND_PICKAXE,
                Material.DIAMOND_AXE,
                Material.DIAMOND_SHOVEL,
                Material.SHEARS
        };
        for (Material tool : tools) {
            ItemStack item = new ItemStack(tool);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setUnbreakable(true);
                item.setItemMeta(meta);
            }
            p.getInventory().addItem(item);
        }
        p.updateInventory();
    }
}