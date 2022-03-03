package net.shortninja.staffplus.server.compatibility.v1_1x;

import be.garagepoort.staffplusplus.craftbukkit.common.json.rayzr.JSONMessage;
import be.garagepoort.staffplusplus.craftbukkit.common.json.rayzr.JsonSender;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundChatPacket;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class JsonSender_v1_18_R0 implements JsonSender {

    @Override
    public void send(JSONMessage jsonMessage, Player...players) {
        sendPacket(createTextPacket(jsonMessage.toString()), players);
    }

    private void sendPacket(ClientboundChatPacket packet, Player... players) {
        if (packet == null) {
            return;
        }

        for (Player player : players) {
            try {
                ((CraftPlayer) player).getHandle().connection.connection.send(packet);
            } catch (Exception e) {
                System.err.println("Failed to send packet");
                e.printStackTrace();
            }
        }
    }

    private ClientboundChatPacket createTextPacket(String message) {
        try {
            ClientboundChatPacket packet = new ClientboundChatPacket(Component.Serializer.fromJson(message), ChatType.CHAT, UUID.randomUUID());
            return packet;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
