package me.junny.dutytoggle.commands;

import be.garagepoort.mcioc.IocCommandHandler;
import be.garagepoort.mcioc.configuration.ConfigProperty;
import me.junny.dutytoggle.DutyToggle;
import me.junny.dutytoggle.util.DutyService;
import me.junny.dutytoggle.util.PermissionHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@IocCommandHandler("offleave")
public class OffLeaveCommand extends AbstractCommand {
    @ConfigProperty("permissions.leave")
    private String permissionLeave;

    private final DutyService dutyService;
    private final PermissionHandler permissionHandler;

    public OffLeaveCommand(DutyService dutyService, PermissionHandler permissionHandler) {
        this.dutyService = dutyService;
        this.permissionHandler = permissionHandler;
    }

    @Override
    public boolean executeCommand(CommandSender sender, Command cmd, String label, String[] args) {
        permissionHandler.validate(sender, permissionLeave);
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Bukkit.getScheduler().runTaskAsynchronously(DutyToggle.plugin, () -> {
                if (dutyService.isOnLeave(player)) {
                    dutyService.offLeave(player);
                    sender.sendMessage(dutyService.getMessage("off-leave"));
                } else {
                    sender.sendMessage(dutyService.getMessage("not-on-leave"));
                }
            });
        }
        return true;
    }
}
