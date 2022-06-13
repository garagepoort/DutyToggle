package me.junny.dutytoggle.commands;

import be.garagepoort.mcioc.IocCommandHandler;
import be.garagepoort.mcioc.configuration.ConfigProperty;
import me.junny.dutytoggle.DutySession;
import me.junny.dutytoggle.DutyToggle;
import me.junny.dutytoggle.bungee.BungeeClient;
import me.junny.dutytoggle.repository.SessionRepository;
import me.junny.dutytoggle.util.DutyService;
import me.junny.dutytoggle.util.PermissionHandler;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.bukkit.Bukkit.getScheduler;

@IocCommandHandler("staffstatus")
public class StaffStatusCommand extends AbstractCommand {
    @ConfigProperty("permissions.staffstatus")
    private String permissionStaffStatus;
    private final DutyService dutyService;
    private final BungeeClient bungeeClient;
    private final SessionRepository sessionRepository;
    private final PermissionHandler permissionHandler;

    public StaffStatusCommand(DutyService dutyService, BungeeClient bungeeClient, SessionRepository sessionRepository, PermissionHandler permissionHandler) {
        this.dutyService = dutyService;
        this.bungeeClient = bungeeClient;
        this.sessionRepository = sessionRepository;
        this.permissionHandler = permissionHandler;
    }

    @Override
    public boolean executeCommand(CommandSender sender, Command cmd, String label, String[] args) {
        permissionHandler.validate(sender, permissionStaffStatus);

        bungeeClient.getPlayers(sender, (allPlayers) ->
            getScheduler().runTaskAsynchronously(DutyToggle.plugin, () -> {
                List<String> staffNames = dutyService.getAllStaffUsers();
                List<DutySession> sessions = sessionRepository.getAllSessions();

                List<String> onDuty = Arrays.stream(allPlayers)
                    .filter(s -> staffNames.stream().anyMatch(staff -> staff.equalsIgnoreCase(s)))
                    .collect(Collectors.toList());

                List<OfflinePlayer> offDuty = sessions.stream()
                    .filter(s -> s.back == 0).map(s -> s.player)
                    .collect(Collectors.toList());

                List<OfflinePlayer> onLeave = sessions.stream()
                    .filter(s -> s.back != 0).map(s -> s.player)
                    .collect(Collectors.toList());

                sender.sendMessage(dutyService.color("&eOn duty:"));
                for (String player : onDuty) {
                    sender.sendMessage(dutyService.color("&7" + player));
                }

                sender.sendMessage(dutyService.color(" "));
                sender.sendMessage(dutyService.color("&eOff duty:"));
                for (OfflinePlayer player : offDuty) {
                    sender.sendMessage(dutyService.color("&7" + player.getName()));
                }

                sender.sendMessage(dutyService.color(" "));
                sender.sendMessage(dutyService.color("&eOn leave:"));
                for (OfflinePlayer player : onLeave) {
                    sender.sendMessage(dutyService.color("&7" + player.getName()));
                }
            })
        );
        return true;
    }
}
