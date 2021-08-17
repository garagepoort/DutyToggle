package me.junny.dutytoggle.commands;

import me.junny.dutytoggle.DutySession;
import me.junny.dutytoggle.DutyToggle;
import me.junny.dutytoggle.bungee.BungeeClient;
import me.junny.dutytoggle.repository.SessionRepository;
import me.junny.dutytoggle.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StaffStatusCommand implements CommandExecutor {

    private static final String BLOCKSTACKERS_STAFF = "blockstackers.staff";

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("staffstatus")) {

            BungeeClient.instance().getPlayers(sender, (allPlayers) ->
                    Bukkit.getScheduler().runTaskAsynchronously(DutyToggle.plugin, () -> {
                        List<String> staffNames = Util.getAllStaffUsers();
                        List<DutySession> sessions = SessionRepository.instance().getAllSessions();

                        List<String> onDuty = Arrays.stream(allPlayers)
                                .filter(s -> staffNames.stream().anyMatch(staff -> staff.equalsIgnoreCase(s)))
                                .collect(Collectors.toList());

                        List<OfflinePlayer> offDuty = sessions.stream()
                                .filter(s -> s.back == 0).map(s -> s.player)
                                .collect(Collectors.toList());

                        List<OfflinePlayer> onLeave = sessions.stream()
                                .filter(s -> s.back != 0).map(s -> s.player)
                                .collect(Collectors.toList());

                        sender.sendMessage(Util.color("&eOn duty:"));
                        for (String player : onDuty) {
                            sender.sendMessage(Util.color("&7" + player));
                        }

                        sender.sendMessage(Util.color(" "));
                        sender.sendMessage(Util.color("&eOff duty:"));
                        for (OfflinePlayer player : offDuty) {
                            sender.sendMessage(Util.color("&7" + player.getName()));
                        }

                        sender.sendMessage(Util.color(" "));
                        sender.sendMessage(Util.color("&eOn leave:"));
                        for (OfflinePlayer player : onLeave) {
                            sender.sendMessage(Util.color("&7" + player.getName()));
                        }

                    })
            );
        }
        return false;
    }
}
