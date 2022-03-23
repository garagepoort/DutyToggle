package me.junny.dutytoggle.commands;

import be.garagepoort.mcioc.IocBean;
import be.garagepoort.mcioc.IocCommandHandler;
import me.junny.dutytoggle.util.DutyService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@IocBean
@IocCommandHandler("offduty")
public class OffDutyCommand implements CommandExecutor {
    private final DutyService dutyService;

    public OffDutyCommand(DutyService dutyService) {
        this.dutyService = dutyService;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (dutyService.isStaff(player)) {
                if (dutyService.isOffDuty(player)) {
                    sender.sendMessage(dutyService.getMessage("not-on-duty"));
                } else {
                    dutyService.offDuty(player);
                    sender.sendMessage(dutyService.getMessage("off-duty"));
                }
            } else {
                sender.sendMessage(dutyService.getMessage("no-permissions"));
            }
        }
        return true;
    }
}
