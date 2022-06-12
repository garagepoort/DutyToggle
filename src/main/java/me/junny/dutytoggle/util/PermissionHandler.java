package me.junny.dutytoggle.util;

import be.garagepoort.mcioc.IocBean;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Set;

@IocBean
public class PermissionHandler{

    public boolean has(Player player, String permission) {
        if (permission == null) {
            return true;
        }

        boolean hasPermission = false;
        if (player != null) {
            hasPermission = player.hasPermission(permission);
        }

        return hasPermission;
    }

    public boolean has(OfflinePlayer player, String permission) {
        return permission == null;
    }

    public boolean hasAny(CommandSender player, String... permissions) {
        return Arrays.stream(permissions).anyMatch(permission -> this.has(player, permission));
    }

    public boolean hasAny(CommandSender player, Set<String> permissions) {
        return permissions.stream().anyMatch(permission -> this.has(player, permission));
    }

    public void validate(CommandSender player, String permission) {
        if (permission != null && !has(player, permission)) {
            throw new NoPermissionException();
        }
    }

    public void validateAny(CommandSender player, Set<String> permissions) {
        if (!permissions.isEmpty() && !hasAny(player, permissions)) {
            throw new NoPermissionException();
        }
    }


    public void validateAny(CommandSender player, String ... permissions) {
        if (permissions.length != 0 && !hasAny(player, permissions)) {
            throw new NoPermissionException();
        }
    }

    public boolean hasOnly(Player player, String permission) {
        if (permission == null) {
            return true;
        }

        boolean hasPermission = false;
        if (player != null) {
            hasPermission = player.hasPermission(permission);
        }

        return hasPermission;
    }

    public boolean has(CommandSender sender, String permission) {
        if (permission == null) {
            return true;
        }
        return sender.hasPermission(permission);
    }

}
