package me.junny.dutytoggle.util;

import be.garagepoort.mcioc.IocBean;
import be.garagepoort.mcioc.configuration.ConfigProperty;
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

@IocBean
public class DutyService {

    @ConfigProperty("staff-groups")
    private List<String> staffGroups;
    @ConfigProperty("mail-players")
    private List<String> mailPlayers;
    @ConfigProperty("off-duty-group")
    private String offDutyGroup;

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

    public User getPlayerAsUser(Player player) {
        return DutyToggle.api.getPlayerAdapter(Player.class).getUser(player);
    }

    public Group getGroup(UUID uuid) {
        return getGroup(DutyToggle.api.getUserManager().getUser(uuid).getPrimaryGroup());
    }

    public Group getGroup(String id) {
        return DutyToggle.api.getGroupManager().getGroup(id);
    }

    public void addGroup(UUID uuid, String group) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            throw new RuntimeException("No players found with uuid: [" + uuid + "]");
        }
        String command = String.format("lp user %s parent add %s", player.getName(), group);
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
    }

    public void removeGroup(UUID uuid, String group) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            throw new RuntimeException("No players found with uuid: [" + uuid + "]");
        }
        String command = String.format("lp user %s parent remove %s", player.getName(), group);
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
    }

    private DutySession getSession(OfflinePlayer player) {
        return sessionRepository.getSession(player.getUniqueId()).orElse(null);
    }

    public boolean isOnLeave(OfflinePlayer player) {
        DutySession session = getSession(player);

        if (session == null) return false;
        return session.back != 0L;
    }

    public boolean isOffDuty(OfflinePlayer player) {
        DutySession session = getSession(player);

        if (session == null) return false;
        return session.back == 0L;
    }

    public void onDuty(OfflinePlayer player) {
        DutySession session = getSession(player);
        addGroup(player.getUniqueId(), session.groupName);
        sessionRepository.deleteSession(player.getUniqueId());
    }

    public void offDuty(OfflinePlayer player) {
        Group group = getGroup(player.getUniqueId());
        DutySession dutySession = new DutySession(player, group.getName());
        removeGroup(player.getUniqueId(), group.getName());
        addGroup(player.getUniqueId(), getGroup(offDutyGroup).getName());

        sessionRepository.saveSession(dutySession);
    }

    public void onLeave(OfflinePlayer player, int days) {
        long daysInMilli = days * 86400000L;
        DutySession dutySession = new DutySession(player, getGroup(player.getUniqueId()).getName(), System.currentTimeMillis() + daysInMilli);
        removeGroup(player.getUniqueId(), getGroup(player.getUniqueId()).getName());
        addGroup(player.getUniqueId(), getGroup(offDutyGroup).getName());
        sessionRepository.saveSession(dutySession);
        mailPlayers(getMessage("mail-on-leave").replace("%player%", player.getName()));
    }

    public void offLeave(OfflinePlayer player) {
        DutySession dutySession = sessionRepository.getSession(player.getUniqueId())
            .orElseThrow(() -> new RuntimeException("No session found for user [" + player.getName() + "]"));

        addGroup(player.getUniqueId(), dutySession.groupName);
        sessionRepository.deleteSession(player.getUniqueId());
        mailPlayers(getMessage("mail-off-leave").replace("%player%", player.getName()));
    }

    public List<Player> getMailPlayers() {
        List<Player> players = new ArrayList<>();
        mailPlayers.forEach(it -> players.add(Bukkit.getPlayer(it)));

        return players;
    }

    public void mailPlayers(String msg) {
        //This won't work because there is no actual command being executed.
        for (Player player : getMailPlayers()) {
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), msg);
        }
    }

    public boolean isStaff(Player player) {
        if (sessionRepository.getSession(player.getUniqueId()).isPresent()) {
            return true;
        }

        return player.hasPermission("blockstackers.staff");
    }

    public List<String> getAllStaffUsers() {
        List<String> groupIds = staffGroups.stream()
            .map(g -> "group." + g)
            .collect(Collectors.toList());

        // Search all users for a match
        return luckPermsRepository.getAllNamesWithPrimaryGroup(groupIds);
    }
}
