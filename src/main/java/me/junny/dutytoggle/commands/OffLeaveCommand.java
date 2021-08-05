package me.junny.dutytoggle.commands;

import me.junny.dutytoggle.util.Util;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OffLeaveCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("offleave")) {
            if(sender instanceof Player) {
                Player player = (Player) sender;

                if(Util.isStaff(player)) {
                    if(Util.isOnLeave(player)) {
                        Util.offLeave(player);
                        sender.sendMessage(Util.getMessage("off-leave"));
                    } else {
                        sender.sendMessage(Util.getMessage("not-on-leave"));
                    }
                } else {
                    sender.sendMessage(Util.getMessage("no-permissions"));
                }
            }
        }
        return false;
    }
}
