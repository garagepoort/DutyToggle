package me.junny.dutytoggle.commands;

import be.garagepoort.mcioc.IocCommandHandler;
import me.junny.dutytoggle.util.DutyService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@IocCommandHandler("onleave")
public class OnLeaveCommand implements CommandExecutor {
    private final DutyService dutyService;

    public OnLeaveCommand(DutyService dutyService) {
        this.dutyService = dutyService;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (dutyService.isStaff(player)) {
                if (args.length == 1) {
                    if (dutyService.isOnLeave(player)) {
                        sender.sendMessage(dutyService.getMessage("not-off-leave"));
                    } else {
                        dutyService.onLeave(player, Integer.parseInt(args[0]));
                        sender.sendMessage(dutyService.getMessage("on-leave"));
                    }
                } else {
                    sender.sendMessage(dutyService.color("&c/onleave <amount of days>"));
                }
            } else {
                sender.sendMessage(dutyService.getMessage("no-permissions"));
            }
        }
        return true;
    }
}
