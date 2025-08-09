package yd.kingdom.main.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import yd.kingdom.main.util.MessageUtil;

public class SpawnCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        // 고정 월드 이름(네 프로젝트에서 기본으로 쓰는 "world")
        World world = Bukkit.getWorld("world");
        if (world == null) {
            MessageUtil.send(sender, "§c월드 'world'를 찾지 못했습니다.");
            return true;
        }

        // 고정 좌표 + 시점 (yaw=180, pitch=0)
        Location target = new Location(world, 5, -58, 31, 180f, 0f);

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.teleport(target);
        }
        return true;
    }
}