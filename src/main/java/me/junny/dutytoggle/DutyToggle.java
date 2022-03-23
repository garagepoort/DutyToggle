package me.junny.dutytoggle;

import be.garagepoort.mcioc.IocBeanProvider;
import be.garagepoort.mcioc.TubingConfiguration;
import be.garagepoort.mcioc.TubingPlugin;
import be.garagepoort.mcsqlmigrations.DatabaseType;
import be.garagepoort.mcsqlmigrations.SqlConnectionProvider;
import be.garagepoort.mcsqlmigrations.helpers.QueryBuilderFactory;
import me.junny.dutytoggle.bungee.BungeeClient;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

@TubingConfiguration
public final class DutyToggle extends TubingPlugin {
    public static DutyToggle plugin;
    public static LuckPerms api;

    @Override
    protected void beforeEnable() {
        plugin = this;
    }

    @Override
    protected void enable() {
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) api = provider.getProvider();
        Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(DutyToggle.plugin, BungeeClient.BUNGEE_CORD_CHANNEL);
        getLogger().info("DutyToggle enabled");
    }

    @Override
    protected void disable() {
        getLogger().info("DutyToggle disabled");
    }

    @IocBeanProvider
    public static QueryBuilderFactory queryBuilderFactory(SqlConnectionProvider sqlConnectionProvider) {
        return new QueryBuilderFactory(DatabaseType.MYSQL, sqlConnectionProvider);
    }
}
