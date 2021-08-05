package me.junny.dutytoggle.commands;

import me.junny.dutytoggle.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class StaffStatusCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("staffstatus")) {
            List<Player> staff = new ArrayList<>();

            for(Player player : Bukkit.getOnlinePlayers()) {
                if(Util.isStaff(player)) staff.add(player);
            }

            List<Player> onDuty = new ArrayList<>();
            List<Player> offDuty = new ArrayList<>();
            List<Player> onLeave = new ArrayList<>();

            for(Player player : staff) {
                if(Util.isOnLeave(player)) {
                    onLeave.add(player);
                } else if(Util.isOffDuty(player)) {
                    offDuty.add(player);
                } else {
                    onDuty.add(player);
                }
            }

            sender.sendMessage(Util.color("&eOn duty:"));
            for(Player player : onDuty) {
                sender.sendMessage(Util.color("&7" + player.getName()));
            }

            sender.sendMessage(Util.color(" "));
            sender.sendMessage(Util.color("&eOff duty:"));
            for(Player player : offDuty) {
                sender.sendMessage(Util.color("&7" + player.getName()));
            }

            sender.sendMessage(Util.color(" "));
            sender.sendMessage(Util.color("&eOn leave:"));
            for(Player player : onLeave) {
                sender.sendMessage(Util.color("&7" + player.getName()));
            }
        }
        return false;
    }
}
