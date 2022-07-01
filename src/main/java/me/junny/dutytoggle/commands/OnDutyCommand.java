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

@IocBukkitCommandHandler("onduty")
public class OnDutyCommand extends AbstractCommand {
    @ConfigProperty("permissions.duty")
    private String permissionDuty;

    private final DutyService dutyService;
    private final PermissionHandler permissionHandler;

    public OnDutyCommand(DutyService dutyService, PermissionHandler permissionHandler) {
        this.dutyService = dutyService;
        this.permissionHandler = permissionHandler;
    }

    @Override
    public boolean executeCommand(CommandSender sender, Command cmd, String label, String[] args) {
        permissionHandler.validate(sender, permissionDuty);
        if (sender instanceof Player) {
            Player player = (Player) sender;

            getScheduler().runTaskAsynchronously(DutyToggle.plugin, () -> {
                if (dutyService.isOffDuty(player)) {
                    dutyService.onDuty(player);
                    sender.sendMessage(dutyService.getMessage("on-duty"));
                } else {
                    sender.sendMessage(dutyService.getMessage("not-off-duty"));
                }
            });
        }
        return true;
    }
}
