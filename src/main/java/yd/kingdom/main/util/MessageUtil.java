package yd.kingdom.main.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class MessageUtil {
    public static void send(CommandSender sender, String msg) {
        sender.sendMessage(ChatColor.GREEN + "[오합지졸] " + ChatColor.WHITE + msg);
    }
}