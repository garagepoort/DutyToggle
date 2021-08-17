package me.junny.dutytoggle.util;

import me.junny.dutytoggle.DutySession;
import me.junny.dutytoggle.DutyToggle;
import me.junny.dutytoggle.repository.LuckPermsRepository;
import me.junny.dutytoggle.repository.SessionRepository;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class Util {

    private static final SessionRepository sessionRepository = SessionRepository.instance();
    private static FileManager fileManager = FileManager.instance();

    public static String color(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public static String getMessage(String id) {
        return color(fileManager.getConfig("config.yml").get().getString("messages." + id));
    }

    public static User getPlayerAsUser(Player player) {
        return DutyToggle.api.getPlayerAdapter(Player.class).getUser(player);
    }

    public static Group getGroup(UUID uuid) {
        return getGroup(DutyToggle.api.getUserManager().getUser(uuid).getPrimaryGroup());
    }

    public static Group getGroup(String id) {
        return DutyToggle.api.getGroupManager().getGroup(id);
    }

    public static void addGroup(UUID uuid, String group) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            throw new RuntimeException("No players found with uuid: [" + uuid + "]");
        }
        String command = String.format("lp user %s parent add %s", player.getName(), group);
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
    }

    public static void removeGroup(UUID uuid, String group) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            throw new RuntimeException("No players found with uuid: [" + uuid + "]");
        }
        String command = String.format("lp user %s parent remove %s", player.getName(), group);
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
    }

    private static DutySession getSession(OfflinePlayer player) {
        return sessionRepository.getSession(player.getUniqueId()).orElse(null);
    }

    public static boolean isOnLeave(OfflinePlayer player) {
        DutySession session = getSession(player);

        if (session == null) return false;
        return session.back != 0L;
    }

    public static boolean isOffDuty(OfflinePlayer player) {
        DutySession session = getSession(player);

        if (session == null) return false;
        return session.back == 0L;
    }

    public static void onDuty(OfflinePlayer player) {
        DutySession session = getSession(player);
        addGroup(player.getUniqueId(), session.groupName);
        sessionRepository.deleteSession(player.getUniqueId());
    }

    public static void offDuty(OfflinePlayer player) {
        Group group = getGroup(player.getUniqueId());
        DutySession dutySession = new DutySession(player, group.getName());
        removeGroup(player.getUniqueId(), group.getName());
        addGroup(player.getUniqueId(), DutyToggle.offDutyGroup.getName());

        sessionRepository.saveSession(dutySession);
    }

    public static void onLeave(OfflinePlayer player, int days) {
        long daysInMilli = days * 86400000L;
        DutySession dutySession = new DutySession(player, getGroup(player.getUniqueId()).getName(), System.currentTimeMillis() + daysInMilli);
        removeGroup(player.getUniqueId(), getGroup(player.getUniqueId()).getName());
        addGroup(player.getUniqueId(), DutyToggle.offDutyGroup.getName());
        sessionRepository.saveSession(dutySession);
        mailPlayers(getMessage("mail-on-leave").replace("%player%", player.getName()));
    }

    public static void offLeave(OfflinePlayer player) {
        DutySession dutySession = sessionRepository.getSession(player.getUniqueId())
                .orElseThrow(() -> new RuntimeException("No session found for user [" + player.getName() + "]"));

        addGroup(player.getUniqueId(), dutySession.groupName);
        sessionRepository.deleteSession(player.getUniqueId());
        mailPlayers(getMessage("mail-off-leave").replace("%player%", player.getName()));
    }

    public static List<Player> getMailPlayers() {
        List<String> playerIds = fileManager.getConfig("config.yml").get().getStringList("mail-players");

        List<Player> players = new ArrayList<>();
        playerIds.forEach(it -> players.add(Bukkit.getPlayer(it)));

        return players;
    }

    public static void mailPlayers(String msg) {
        //This won't work because there is no actual command being executed.
        for (Player player : getMailPlayers()) {
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), msg);
        }
    }

    public static boolean isStaff(Player player) {
        if (sessionRepository.getSession(player.getUniqueId()).isPresent()) {
            return true;
        }

        return player.hasPermission("blockstackers.staff");
    }

    public static List<String> getAllStaffUsers() {
        List<String> groupIds = fileManager.getConfig("config.yml").get().getStringList("staff-groups")
                .stream().map(g -> "group." + g).collect(Collectors.toList());

        // Search all users for a match
        return LuckPermsRepository.instance().getAllNamesWithPrimaryGroup(groupIds);
    }
}
