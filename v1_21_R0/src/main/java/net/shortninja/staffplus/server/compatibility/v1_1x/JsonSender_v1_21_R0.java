package net.shortninja.staffplus.server.compatibility.v1_1x;

import be.garagepoort.staffplusplus.craftbukkit.common.json.rayzr.JSONMessage;
import be.garagepoort.staffplusplus.craftbukkit.common.json.rayzr.JsonSender;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import org.bukkit.craftbukkit.v1_21_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_21_R1.entity.CraftPlayer;

import java.lang.reflect.Method;

public class JsonSender_v1_21_R0 implements JsonSender {
    private static final ServerLevel SERVER_LEVEL = ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle(); // The overworld

    @Override
    public void send(JSONMessage jsonMessage, Player...players) {
        sendPacket(createTextPacket(jsonMessage.toString()), players);
    }

    private void sendPacket(ClientboundSystemChatPacket packet, Player... players) {
        if (packet == null) {
            return;
        }
        
        // The 2 `send` methods are called different in obfuscated code, so we have to do this workaround to get the correct one
        Method sendMethod;
        try {
            Class[] args = new Class[1];
            args[0] = Packet.class;
            sendMethod = ServerGamePacketListenerImpl.class.getMethod("b", args);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Failed to get `send` method on ServerGamePacketListenerImpl", e);
        }
        
        for (Player player : players) {
            ServerGamePacketListenerImpl serverGamePacketListener = ((CraftPlayer) player).getHandle().connection;
            
            try {
                sendMethod.invoke(serverGamePacketListener, packet);
            } catch (Exception e) {
                throw new RuntimeException("Failed to send packet", e);
            }
        }
    }

    private ClientboundSystemChatPacket createTextPacket(String message) {
        try {
            return new ClientboundSystemChatPacket(Component.Serializer.fromJson(message, SERVER_LEVEL.registryAccess()), false);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize json packet message. Is it valid JSON?", e);
        }
    }
}
