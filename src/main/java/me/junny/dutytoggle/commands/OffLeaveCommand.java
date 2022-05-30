package me.junny.dutytoggle.commands;

import be.garagepoort.mcioc.IocCommandHandler;
import me.junny.dutytoggle.util.DutyService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@IocCommandHandler("offleave")
public class OffLeaveCommand implements CommandExecutor {
    private final DutyService dutyService;

    public OffLeaveCommand(DutyService dutyService) {
        this.dutyService = dutyService;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (dutyService.isStaff(player)) {
                if (dutyService.isOnLeave(player)) {
                    dutyService.offLeave(player);
                    sender.sendMessage(dutyService.getMessage("off-leave"));
                } else {
                    sender.sendMessage(dutyService.getMessage("not-on-leave"));
                }
            } else {
                sender.sendMessage(dutyService.getMessage("no-permissions"));
            }
        }
        return true;
    }
}
