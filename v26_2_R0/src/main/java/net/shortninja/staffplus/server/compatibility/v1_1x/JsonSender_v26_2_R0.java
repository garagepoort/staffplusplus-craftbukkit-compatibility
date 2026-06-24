package net.shortninja.staffplus.server.compatibility.v1_1x;

import be.garagepoort.staffplusplus.craftbukkit.common.json.rayzr.JSONMessage;
import be.garagepoort.staffplusplus.craftbukkit.common.json.rayzr.JsonSender;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

public class JsonSender_v26_2_R0 implements JsonSender {

    @Override
    public void send(JSONMessage jsonMessage, Player...players) {
        sendPacket(createTextPacket(jsonMessage.toString()), players);
    }

    private void sendPacket(ClientboundSystemChatPacket packet, Player... players) {
        if (packet == null) {
            return;
        }

        for (Player player : players) {
            ServerGamePacketListenerImpl serverGamePacketListener = ((CraftPlayer) player).getHandle().connection;
            serverGamePacketListener.send(packet);
        }
    }

    private ClientboundSystemChatPacket createTextPacket(String message) {
        try {
            return new ClientboundSystemChatPacket(CraftChatMessage.fromJSON(message), false);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize json packet message. Is it valid JSON?", e);
        }
    }
}
