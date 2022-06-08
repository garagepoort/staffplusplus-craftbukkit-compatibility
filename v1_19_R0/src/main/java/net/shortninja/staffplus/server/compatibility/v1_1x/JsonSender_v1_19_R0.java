package net.shortninja.staffplus.server.compatibility.v1_1x;

import be.garagepoort.staffplusplus.craftbukkit.common.json.rayzr.JSONMessage;
import be.garagepoort.staffplusplus.craftbukkit.common.json.rayzr.JsonSender;
import net.minecraft.network.chat.ChatSender;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundPlayerChatPacket;
import net.minecraft.util.Crypt;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public class JsonSender_v1_19_R0 implements JsonSender {

    @Override
    public void send(JSONMessage jsonMessage, Player...players) {
        sendPacket(createTextPacket(jsonMessage.toString()), players);
    }

    private void sendPacket(ClientboundPlayerChatPacket packet, Player... players) {
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

    private ClientboundPlayerChatPacket createTextPacket(String message) {
        try {
            return new ClientboundPlayerChatPacket(Component.Serializer.fromJson(message), Optional.empty(), 0, ChatSender.system(Component.literal("Staff++")), Instant.now(), Crypt.SaltSignaturePair.EMPTY);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
