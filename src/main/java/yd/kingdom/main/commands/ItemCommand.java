package yd.kingdom.main.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import yd.kingdom.main.Main;
import yd.kingdom.main.game.ItemManager;
import yd.kingdom.main.util.MessageUtil;

public class ItemCommand implements CommandExecutor {
    private final ItemManager itemManager = Main.getInstance().getItemManager();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd,
                             String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("플레이어만 사용할 수 있습니다.");
            return true;
        }
        if (args.length < 1) {
            MessageUtil.send(player,
                    "사용법: /아이템 <초기화권 | 깽판권 | 점프부스트 | 좀비소환권 | 감옥권 | 실명권>");
            return false;
        }
        String key = args[0].toLowerCase();
        ItemStack item = itemManager.getItemByKey(key);
        if (item == null) {
            MessageUtil.send(player, "존재하지 않는 아이템입니다: " + key);
            return true;
        }
        player.getInventory().addItem(item);
        MessageUtil.send(player, key + " 아이템을 지급했습니다.");
        return true;
    }
}
