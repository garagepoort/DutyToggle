package me.junny.dutytoggle.commands;

import me.junny.dutytoggle.DutySession;
import me.junny.dutytoggle.repository.SessionRepository;
import me.junny.dutytoggle.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StaffStatusCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("staffstatus")) {

            List<DutySession> sessions = SessionRepository.instance().getAllSessions();

            // TODO retrieve all players from the velocity network
            List<Player> onDuty = Bukkit.getOnlinePlayers().stream()
                    .filter(p -> sessions.stream().noneMatch(s -> s.player.getUniqueId() == p.getUniqueId()))
                    .filter(Util::isStaff)
                    .collect(Collectors.toList());

            List<OfflinePlayer> offDuty = sessions.stream()
                    .filter(s -> s.back == 0).map(s -> s.player)
                    .collect(Collectors.toList());

            List<OfflinePlayer> onLeave = sessions.stream()
                    .filter(s -> s.back != 0).map(s -> s.player)
                    .collect(Collectors.toList());

            sender.sendMessage(Util.color("&eOn duty:"));
            for (Player player : onDuty) {
                sender.sendMessage(Util.color("&7" + player.getName()));
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
        }
        return false;
    }
}
