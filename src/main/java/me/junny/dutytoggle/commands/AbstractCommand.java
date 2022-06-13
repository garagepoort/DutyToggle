package me.junny.dutytoggle.commands;

import be.garagepoort.mcioc.configuration.ConfigProperty;
import me.junny.dutytoggle.util.DutyToggleException;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public abstract class AbstractCommand implements CommandExecutor {

    @ConfigProperty("messages.no-permissions")
    private String noPermissions;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        try {
            return executeCommand(commandSender, command, s, strings);
        } catch (DutyToggleException e) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', noPermissions));
            return false;
        }
    }

    public abstract boolean executeCommand(CommandSender commandSender, Command command, String s, String[] strings);
}
