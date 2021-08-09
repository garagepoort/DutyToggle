package me.junny.dutytoggle;

import net.luckperms.api.model.group.Group;
import org.bukkit.OfflinePlayer;

public class DutySession {
    public int id;
    public OfflinePlayer player;
    public String groupName;
    public long back;

    public DutySession(int id, OfflinePlayer player, String groupName, long back) {
        this.id = id;
        this.player = player;
        this.groupName = groupName;
        this.back = back;
    }

    public DutySession(OfflinePlayer player, String groupName) {
        this.player = player;
        this.groupName = groupName;
        this.back = 0;
    }

    public DutySession(OfflinePlayer player, String groupName, long back) {
        this.player = player;
        this.groupName = groupName;
        this.back = back;
    }
}
