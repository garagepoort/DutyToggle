package me.junny.dutytoggle.commands;

import be.garagepoort.mcioc.IocCommandHandler;
import be.garagepoort.mcioc.configuration.ConfigProperty;
import me.junny.dutytoggle.DutyToggle;
import me.junny.dutytoggle.util.DutyService;
import me.junny.dutytoggle.util.PermissionHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static org.bukkit.Bukkit.getScheduler;

@IocCommandHandler("offduty")
public class OffDutyCommand extends AbstractCommand {
    @ConfigProperty("permissions.duty")
    private String permissionDuty;

    private final DutyService dutyService;
    private final PermissionHandler permissionHandler;

    public OffDutyCommand(DutyService dutyService, PermissionHandler permissionHandler) {
        this.dutyService = dutyService;
        this.permissionHandler = permissionHandler;
    }

    @Override
    public boolean executeCommand(CommandSender sender, Command cmd, String label, String[] args) {
        permissionHandler.validate(sender, permissionDuty);
        if (sender instanceof Player) {
            getScheduler().runTaskAsynchronously(DutyToggle.plugin, () -> {
                Player player = (Player) sender;
                if (dutyService.isOffDuty(player)) {
                    sender.sendMessage(dutyService.getMessage("not-on-duty"));
                } else {
                    dutyService.offDuty(player, 0);
                    sender.sendMessage(dutyService.getMessage("off-duty"));
                }
            });
        }
        return true;
    }
}
