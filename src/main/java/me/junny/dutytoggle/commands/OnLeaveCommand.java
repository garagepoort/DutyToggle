package me.junny.dutytoggle.commands;

import be.garagepoort.mcioc.tubingbukkit.annotations.IocBukkitCommandHandler;
import be.garagepoort.mcioc.configuration.ConfigProperty;
import me.junny.dutytoggle.DutyToggle;
import me.junny.dutytoggle.util.DutyService;
import me.junny.dutytoggle.util.PermissionHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static org.bukkit.Bukkit.getScheduler;

@IocBukkitCommandHandler("onleave")
public class OnLeaveCommand extends AbstractCommand {
    @ConfigProperty("permissions.leave")
    private String permissionLeave;

    private final DutyService dutyService;
    private final PermissionHandler permissionHandler;

    public OnLeaveCommand(DutyService dutyService, PermissionHandler permissionHandler) {
        this.dutyService = dutyService;
        this.permissionHandler = permissionHandler;
    }

    @Override
    public boolean executeCommand(CommandSender sender, Command cmd, String label, String[] args) {
        permissionHandler.validate(sender, permissionLeave);
        if (sender instanceof Player) {
            Player player = (Player) sender;
            getScheduler().runTaskAsynchronously(DutyToggle.plugin, () -> {
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
            });

        }
        return true;
    }
}
