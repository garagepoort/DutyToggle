package me.junny.dutytoggle.commands;

import me.junny.dutytoggle.util.Util;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OnDutyCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("onduty")) {
            if(sender instanceof Player) {
                Player player = (Player) sender;

                if(Util.isStaff(player)) {
                    if(Util.isOffDuty(player)) {
                        Util.onDuty(player);
                        sender.sendMessage(Util.getMessage("on-duty"));
                    } else {
                        sender.sendMessage(Util.getMessage("not-off-duty"));
                    }
                } else {
                    sender.sendMessage(Util.getMessage("no-permissions"));
                }
            }
        }
        return false;
    }
}
