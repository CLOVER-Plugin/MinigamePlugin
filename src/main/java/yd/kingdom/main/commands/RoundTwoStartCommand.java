package yd.kingdom.main.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import yd.kingdom.main.Main;
import yd.kingdom.main.manager.round2.RoundTwoManager;
import yd.kingdom.main.util.MessageUtil;

public class RoundTwoStartCommand implements CommandExecutor {

    private final RoundTwoManager r2 = Main.getInstance().getRoundTwoManager();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        r2.begin();
        MessageUtil.send(sender, "2라운드 시작");
        return true;
    }
}