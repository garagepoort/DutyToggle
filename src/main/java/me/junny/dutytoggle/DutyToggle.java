package me.junny.dutytoggle;

import me.junny.dutytoggle.commands.*;
import me.junny.dutytoggle.util.FileManager;
import me.junny.dutytoggle.util.Util;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public final class DutyToggle extends JavaPlugin {
    public static DutyToggle plugin;
    public static LuckPerms api;
    public static List<DutySession> sessions;

    public static Group offDutyGroup;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;

        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if(provider != null) api = provider.getProvider();

        FileManager fm = new FileManager(this);
        if(!getDataFolder().exists()) {
            fm.getConfig("config.yml").saveDefaultConfig();
        }

        offDutyGroup = Util.getGroup(fm.getConfig("config.yml").get().getString("off-duty-group"));
        sessions = Util.loadSessions();

        // REGISTER COMMANDS
        getCommand("onduty").setExecutor(new OnDutyCommand());
        getCommand("offduty").setExecutor(new OffDutyCommand());
        getCommand("onleave").setExecutor(new OnLeaveCommand());
        getCommand("offleave").setExecutor(new OffLeaveCommand());
        getCommand("staffstatus").setExecutor(new StaffStatusCommand());

        // START TIMER THAT CHECKS FOR WHEN STAFF COMES BACK
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                List<OfflinePlayer> toGoOff = new ArrayList<>();

                for(DutySession session : sessions) {
                    if(session.back != 0L) {
                        if(session.back < System.currentTimeMillis()) {
                            toGoOff.add(session.player);
                        }
                    }
                }

                for(OfflinePlayer player : toGoOff) {
                    Util.offLeave(player);
                }
            }
        }.runTaskTimer(this, 0, 200);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
