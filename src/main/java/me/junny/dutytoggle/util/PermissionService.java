package me.junny.dutytoggle.util;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.plugin.RegisteredServiceProvider;

public class PermissionService {

    private static final String MAIN_WORLD = "world";
    private static Permission perms = null;
    private static PermissionService instance;

    private PermissionService() {
        RegisteredServiceProvider<Permission> rsp = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
        if (rsp == null) {
            throw new RuntimeException("Vault plugin was not found. Please disable vault in the config or provide the vault plugin");
        }
        perms = rsp.getProvider();
    }

    public static PermissionService instance() {
        if (instance == null) {
            instance = new PermissionService();
        }
        return instance;
    }

    public boolean has(OfflinePlayer player, String permission) {
        if (permission == null) {
            return true;
        }

        boolean hasPermission = false;
        if (player != null) {
            World world = Bukkit.getWorlds().stream().filter(w -> w.getName().equalsIgnoreCase(MAIN_WORLD)).findFirst().orElse(Bukkit.getWorlds().get(0));
            hasPermission = perms.playerHas(world.getName(), player, permission);
        }

        return hasPermission;
    }
}
