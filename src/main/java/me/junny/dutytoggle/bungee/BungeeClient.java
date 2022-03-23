package me.junny.dutytoggle.bungee;

import be.garagepoort.mcioc.IocBean;
import be.garagepoort.mcioc.IocMessageListener;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.junny.dutytoggle.DutyToggle;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import static com.google.common.io.ByteStreams.newDataOutput;

@IocBean
@IocMessageListener(channel = BungeeClient.BUNGEE_CORD_CHANNEL)
public class BungeeClient implements PluginMessageListener {

    public static final String BUNGEE_CORD_CHANNEL = "BungeeCord";
    private final List<Consumer<String[]>> receiveHandlers = new ArrayList<>();

    public void getPlayers(CommandSender sender, Consumer<String[]> onPlayersReceived) {
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        } else {
            Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
            if (onlinePlayers.iterator().hasNext()) {
                player = onlinePlayers.iterator().next();
            }
        }
        if (player != null) {
            receiveHandlers.add(onPlayersReceived);
            ByteArrayDataOutput out = newDataOutput();
            out.writeUTF("PlayerList");
            out.writeUTF("ALL");
            player.sendPluginMessage(DutyToggle.plugin, BUNGEE_CORD_CHANNEL, out.toByteArray());
        }
    }

    private String[] getPlayers(byte[] message) {
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subChannel = in.readUTF();
        if (subChannel.equals("PlayerList")) {
            // Need to read the server line.
            String server = in.readUTF();
            String playerList = in.readUTF();
            return playerList.split(", ");
        }
        return new String[]{};
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] bytes) {
        if (!channel.equals(BUNGEE_CORD_CHANNEL)) {
            return;
        }

        for (Consumer<String[]> receiveHandler : receiveHandlers) {
            String[] players = getPlayers(bytes);
            receiveHandler.accept(players);
        }
        receiveHandlers.clear();
    }
}
