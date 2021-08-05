package me.junny.dutytoggle.util;

import me.junny.dutytoggle.DutySession;
import me.junny.dutytoggle.DutyToggle;
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
    public static String color(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public static String getMessage(String id) {
        FileManager fm = new FileManager(DutyToggle.plugin);
        return color(fm.getConfig("config.yml").get().getString("messages." + id));
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
        for(DutySession session : DutyToggle.sessions) {
            if(session.player.equals(player)) return session;
        }
        return null;
    }

    public static boolean isOnLeave(OfflinePlayer player) {
        DutySession session = getSession(player);

        if(session == null) return false;
        return session.back != 0L;
    }

    public static boolean isOffDuty(OfflinePlayer player) {
        DutySession session = getSession(player);

        if(session == null) return false;
        return session.back == 0L;
    }

    public static void onDuty(OfflinePlayer player) {
        setGroup(player.getUniqueId(), getSession(player).group);
        DutyToggle.sessions.remove(getSession(player));
        saveSessions();
    }

    public static void offDuty(OfflinePlayer player) {
        DutyToggle.sessions.add(new DutySession(player, getGroup(player.getUniqueId())));
        setGroup(player.getUniqueId(), DutyToggle.offDutyGroup);
        saveSessions();
    }

    public static void onLeave(OfflinePlayer player, int days) {
        DutyToggle.sessions.add(new DutySession(player, getGroup(player.getUniqueId()), System.currentTimeMillis()+86400000));
        setGroup(player.getUniqueId(), DutyToggle.offDutyGroup);
        saveSessions();

        mailPlayers(getMessage("mail-on-leave").replace("%player%", player.getName()));
    }

    public static void offLeave(OfflinePlayer player) {
        setGroup(player.getUniqueId(), getSession(player).group);
        DutyToggle.sessions.remove(getSession(player));
        saveSessions();

        mailPlayers(getMessage("mail-off-leave").replace("%player%", player.getName()));
    }

    public static List<Player> getMailPlayers() {
        FileManager fm = new FileManager(DutyToggle.plugin);
        List<String> playerIds = fm.getConfig("config.yml").get().getStringList("mail-players");

        List<Player> players = new ArrayList<>();
        playerIds.forEach(it -> players.add(Bukkit.getPlayer(it)));

        return players;
    }

    public static void mailPlayers(String msg) {
        for(Player player : getMailPlayers()) {
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), msg);
        }
    }

    public static List<DutySession> loadSessions() {
        FileManager fm = new FileManager(DutyToggle.plugin);
        List<DutySession> sessions = new ArrayList<>();

        if(fm.getConfig("config.yml").get().contains("data")) {
            for(String uuid : fm.getConfig("config.yml").get().getConfigurationSection("data").getKeys(false)) {
                OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
                Group group = getGroup(fm.getConfig("config.yml").get().getString("data." + uuid + ".group"));

                if(fm.getConfig("config.yml").get().contains("data." + uuid + ".back")) {
                    long back = fm.getConfig("config.yml").get().getLong("data." + uuid + ".back");
                    sessions.add(new DutySession(player, group, back));
                } else {
                    sessions.add(new DutySession(player, group));
                }
            }
        }

        return sessions;
    }

    public static void saveSessions() {
        FileManager fm = new FileManager(DutyToggle.plugin);

        fm.getConfig("config.yml").get().set("data", null);
        fm.saveConfig("config.yml");

        for(DutySession session : DutyToggle.sessions) {
            fm.getConfig("config.yml").get().set("data." + session.player.getUniqueId().toString() + ".group", session.group.getName());
            if(session.back != 0L) fm.getConfig("config.yml").get().set("data." + session.player.getUniqueId().toString() + ".back", session.back);
            fm.saveConfig("config.yml");
        }
    }

    public static boolean isStaff(Player player) {
        FileManager fm = new FileManager(DutyToggle.plugin);
        List<String> groupIds = fm.getConfig("config.yml").get().getStringList("staff-groups");

        List<Group> groups = new ArrayList<>();
        groupIds.forEach(it -> groups.add(getGroup(it)));

        if(!groups.contains(getGroup(player))) {
            return player.hasPermission("dutytoggle.staff");
        } else {
            return true;
        }
    }
}
