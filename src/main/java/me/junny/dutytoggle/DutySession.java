package me.junny.dutytoggle;

import net.luckperms.api.model.group.Group;
import org.bukkit.OfflinePlayer;

public class DutySession {
    public OfflinePlayer player;
    public Group group;
    public long back;

    public DutySession(OfflinePlayer player, Group group) {
        this.player = player;
        this.group = group;
        this.back = 0;
    }

    public DutySession(OfflinePlayer player, Group group, long back) {
        this.player = player;
        this.group = group;
        this.back = back;
    }
}
