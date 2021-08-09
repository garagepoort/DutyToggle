package me.junny.dutytoggle.util;

import me.junny.dutytoggle.DutySession;
import me.junny.dutytoggle.DutyToggle;
import me.junny.dutytoggle.repository.SessionRepository;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    public static User getPlayerAsUser(UUID uuid) {
        return DutyToggle.api.getUserManager().getUser(uuid);
    }

    public static Group getGroup(Player player) {
        return getGroup(getPlayerAsUser(player).getPrimaryGroup());
    }

    public static Group getGroup(UUID uuid) {
        return getGroup(getPlayerAsUser(uuid).getPrimaryGroup());
    }

    public static Group getGroup(String id) {
        return DutyToggle.api.getGroupManager().getGroup(id);
    }

    public static void setGroup(UUID uuid, Group group) {
        User user = getPlayerAsUser(uuid);
        Group old = getGroup(user.getPrimaryGroup());

        user.data().add(InheritanceNode.builder(old.getName()).value(false).build());
        user.data().add(InheritanceNode.builder(group.getName()).value(true).build());

        DutyToggle.api.getUserManager().saveUser(user);
    }

    public static void setGroup(Player player, Group group) {
        User user = getPlayerAsUser(player);
        Group old = getGroup(player);

        user.data().add(InheritanceNode.builder(old.getName()).value(false).build());
        user.data().add(InheritanceNode.builder(group.getName()).value(true).build());

        DutyToggle.api.getUserManager().saveUser(user);
    }

    public static DutySession getSession(OfflinePlayer player) {
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
        setGroup(player.getUniqueId(), getGroup(session.groupName));
        sessionRepository.deleteSession(player.getUniqueId());
    }

    public static void offDuty(OfflinePlayer player) {
        DutySession dutySession = new DutySession(player, getGroup(player.getUniqueId()).getName());
        setGroup(player.getUniqueId(), DutyToggle.offDutyGroup);
        sessionRepository.saveSession(dutySession);
    }

    public static void onLeave(OfflinePlayer player, int days) {
        long daysInMilli = days * 86400000L;
        DutySession dutySession = new DutySession(player, getGroup(player.getUniqueId()).getName(), System.currentTimeMillis() + daysInMilli);
        sessionRepository.saveSession(dutySession);
        setGroup(player.getUniqueId(), DutyToggle.offDutyGroup);
        mailPlayers(getMessage("mail-on-leave").replace("%player%", player.getName()));
    }

    public static void offLeave(OfflinePlayer player) {
        DutySession dutySession = sessionRepository.getSession(player.getUniqueId())
                .orElseThrow(() -> new RuntimeException("No session found for user [" + player.getName() + "]"));
        offLeave(player, dutySession.groupName);
    }

    public static void offLeave(OfflinePlayer player, String groupName) {
        setGroup(player.getUniqueId(), getGroup(groupName));
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
        List<String> groupIds = fileManager.getConfig("config.yml").get().getStringList("staff-groups");

        List<Group> groups = new ArrayList<>();
        groupIds.forEach(it -> groups.add(getGroup(it)));

        if (!groups.contains(getGroup(player))) {
            return player.hasPermission("dutytoggle.staff");
        } else {
            return true;
        }
    }
}
