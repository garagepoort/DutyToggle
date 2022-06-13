package me.junny.dutytoggle;

import org.bukkit.OfflinePlayer;

import java.util.List;

public class DutySession {
    public int id;
    public OfflinePlayer player;
    public List<String> groups;
    public long back;

    public DutySession(int id, OfflinePlayer player, List<String> groups, long back) {
        this.id = id;
        this.player = player;
        this.groups = groups;
        this.back = back;
    }

    public DutySession(OfflinePlayer player, List<String> groups) {
        this.player = player;
        this.groups = groups;
        this.back = 0;
    }

    public DutySession(OfflinePlayer player, List<String> groups, long back) {
        this.player = player;
        this.groups = groups;
        this.back = back;
    }
}
