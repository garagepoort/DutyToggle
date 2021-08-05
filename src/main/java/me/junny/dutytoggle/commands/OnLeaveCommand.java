package me.junny.dutytoggle.commands;

import me.junny.dutytoggle.util.Util;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OnLeaveCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("onleave")) {
            if(sender instanceof Player) {
                Player player = (Player) sender;

                if(Util.isStaff(player)) {
                    if(args.length == 1) {
                        if(Util.isOnLeave(player)) {
                            sender.sendMessage(Util.getMessage("not-off-leave"));
                        } else {
                            Util.onLeave(player, Integer.parseInt(args[0]));
                            sender.sendMessage(Util.getMessage("on-leave"));
                        }
                    } else {
                        sender.sendMessage(Util.color("&c/onleave <amount of days>"));
                    }
                } else {
                    sender.sendMessage(Util.getMessage("no-permissions"));
                }
            }
        }
        return false;
    }
}
