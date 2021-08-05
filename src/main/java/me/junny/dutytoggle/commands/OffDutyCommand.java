package me.junny.dutytoggle.commands;

import me.junny.dutytoggle.util.Util;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OffDutyCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("offduty")) {
            if(sender instanceof Player) {
                Player player = (Player) sender;

                if(Util.isStaff(player)) {
                    if(Util.isOffDuty(player)) {
                        sender.sendMessage(Util.getMessage("not-on-duty"));
                    } else {
                        Util.offDuty(player);
                        sender.sendMessage(Util.getMessage("off-duty"));
                    }
                } else {
                    sender.sendMessage(Util.getMessage("no-permissions"));
                }
            }
        }
        return false;
    }
}
