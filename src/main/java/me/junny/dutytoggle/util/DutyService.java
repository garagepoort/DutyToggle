package me.junny.dutytoggle.util;

import be.garagepoort.mcioc.IocBean;
import be.garagepoort.mcioc.configuration.ConfigProperty;
import me.junny.dutytoggle.DutySession;
import me.junny.dutytoggle.DutyToggle;
import me.junny.dutytoggle.repository.LuckPermsRepository;
import me.junny.dutytoggle.repository.SessionRepository;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@IocBean
public class DutyService {

    @ConfigProperty("staff-groups")
    private List<String> staffGroups;
    @ConfigProperty("mail-players")
    private List<String> mailPlayers;

    private final SessionRepository sessionRepository;
    private final LuckPermsRepository luckPermsRepository;

    public DutyService(SessionRepository sessionRepository, LuckPermsRepository luckPermsRepository) {
        this.sessionRepository = sessionRepository;
        this.luckPermsRepository = luckPermsRepository;
    }

    public String color(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public String getMessage(String id) {
        return color(DutyToggle.getPlugin().getFileConfigurations().get("config").getString("messages." + id));
    }

    public boolean isOnLeave(OfflinePlayer player) {
        return sessionRepository.getSession(player.getUniqueId())
            .filter(dutySession -> dutySession.back != 0L)
            .isPresent();
    }

    public boolean isOffDuty(OfflinePlayer player) {
        return sessionRepository.getSession(player.getUniqueId())
            .filter(dutySession -> dutySession.back == 0L)
            .isPresent();
    }

    public void onDuty(OfflinePlayer player) {
        DutySession session = getSession(player);
        addGroups(player.getUniqueId(), session.groups);
        sessionRepository.deleteSession(player.getUniqueId());
    }

    public void offDuty(OfflinePlayer player, int days) {
        long daysInMilli = days * 86400000L;
        List<String> luckpermGroups = luckPermsRepository.getStaffGroupsForPlayer(player.getUniqueId(), staffGroups);
        DutySession dutySession = new DutySession(player, luckpermGroups, daysInMilli);
        removeGroups(player.getUniqueId(), luckpermGroups);
        sessionRepository.saveSession(dutySession);
    }

    public void onLeave(OfflinePlayer player, int days) {
        offDuty(player, days);
        mailPlayers(getMessage("mail-on-leave").replace("%player%", player.getName()));
    }

    public void offLeave(OfflinePlayer player) {
        onDuty(player);
        mailPlayers(getMessage("mail-off-leave").replace("%player%", player.getName()));
    }

    public List<Player> getMailPlayers() {
        List<Player> players = new ArrayList<>();
        mailPlayers.forEach(it -> players.add(Bukkit.getPlayer(it)));

        return players;
    }

    private void mailPlayers(String msg) {
        //This won't work because there is no actual command being executed.
        for (Player player : getMailPlayers()) {
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), msg);
        }
    }

    public List<String> getAllStaffUsers() {
        List<String> groupIds = staffGroups.stream()
            .map(g -> "group." + g)
            .collect(Collectors.toList());

        // Search all users for a match
        return luckPermsRepository.getAllNamesWithPrimaryGroup(groupIds);
    }

    private void addGroups(UUID uuid, List<String> groups) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            throw new RuntimeException("No players found with uuid: [" + uuid + "]");
        }
        groups.stream()
            .map(staffGroup -> String.format("lp user %s parent add %s", player.getName(), staffGroup))
            .forEach(command -> Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command));
    }

    private void removeGroups(UUID uuid, List<String> groups) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            throw new RuntimeException("No players found with uuid: [" + uuid + "]");
        }
        groups.stream()
            .map(group -> String.format("lp user %s parent remove %s", player.getName(), group))
            .forEach(command -> Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command));
    }

    private DutySession getSession(OfflinePlayer player) {
        return sessionRepository.getSession(player.getUniqueId())
            .orElseThrow(() -> new RuntimeException("No session found for user [" + player.getName() + "]"));
    }

}
