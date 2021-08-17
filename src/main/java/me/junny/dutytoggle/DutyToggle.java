package me.junny.dutytoggle;

import me.junny.dutytoggle.bungee.BungeeClient;
import me.junny.dutytoggle.commands.*;
import me.junny.dutytoggle.repository.DatabaseMigrator;
import me.junny.dutytoggle.repository.SessionRepository;
import me.junny.dutytoggle.util.FileManager;
import me.junny.dutytoggle.util.Util;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public final class DutyToggle extends JavaPlugin {
    public static DutyToggle plugin;
    public static LuckPerms api;

    public static Group offDutyGroup;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;

        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if(provider != null) api = provider.getProvider();

        if(!getDataFolder().exists()) {
            FileManager.instance().getConfig("config.yml").saveDefaultConfig();
        }

        DatabaseMigrator.executeMigrations();

        offDutyGroup = Util.getGroup(FileManager.instance().getConfig("config.yml").get().getString("off-duty-group"));
        Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(DutyToggle.plugin, BungeeClient.BUNGEE_CORD_CHANNEL);
        Bukkit.getServer().getMessenger().registerIncomingPluginChannel(DutyToggle.plugin, BungeeClient.BUNGEE_CORD_CHANNEL, BungeeClient.instance());

        // REGISTER COMMANDS
        getCommand("onduty").setExecutor(new OnDutyCommand());
        getCommand("offduty").setExecutor(new OffDutyCommand());
        getCommand("onleave").setExecutor(new OnLeaveCommand());
        getCommand("offleave").setExecutor(new OffLeaveCommand());
        getCommand("staffstatus").setExecutor(new StaffStatusCommand());

        // START TIMER THAT CHECKS FOR WHEN STAFF COMES BACK
        new BukkitRunnable() {
            @Override
            public void run() {
                List<DutySession> allSessions = SessionRepository.instance().getExpiredSessions();
                allSessions.forEach(s -> Util.offLeave(s.player));
            }
        }.runTaskTimer(this, 0, 200);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
