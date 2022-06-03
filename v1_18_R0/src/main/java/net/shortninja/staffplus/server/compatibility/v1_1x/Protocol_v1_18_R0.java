package net.shortninja.staffplus.server.compatibility.v1_1x;

import be.garagepoort.staffplusplus.craftbukkit.common.IProtocol;
import be.garagepoort.staffplusplus.craftbukkit.common.json.JsonMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundChatPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket.Action;
import net.minecraft.world.item.ItemStack;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.craftbukkit.v1_18_R1.CraftServer;
import org.bukkit.craftbukkit.v1_18_R1.command.CraftCommandMap;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_18_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

public class Protocol_v1_18_R0 implements IProtocol {
    @Override
    public org.bukkit.inventory.ItemStack addNbtString(org.bukkit.inventory.ItemStack item, String value) {
        ItemStack craftItem = CraftItemStack.asNMSCopy(item);
        CompoundTag nbtCompound = craftItem.getTag() == null ? new CompoundTag() : craftItem.getTag();

        nbtCompound.putString(NBT_IDENTIFIER, value);
        craftItem.setTag(nbtCompound);

        return CraftItemStack.asCraftMirror(craftItem);
    }

    @Override
    public String getNbtString(org.bukkit.inventory.ItemStack item) {
        ItemStack craftItem = CraftItemStack.asNMSCopy(item);

        if (craftItem == null) {
            return "";
        }

        CompoundTag nbtCompound = craftItem.getTag() == null ? new CompoundTag() : craftItem.getTag();

        return nbtCompound.getString(NBT_IDENTIFIER);
    }

    @Override
    public void unregisterCommand(String match, Command command) {
        CraftCommandMap commandMap = (CraftCommandMap) ((CraftServer) Bukkit.getServer()).getCommandMap();
        command.unregister(commandMap);

        String commandName = command.getLabel().toLowerCase();
        if (commandMap.getKnownCommands().get(commandName) == command) {
            commandMap.getKnownCommands().remove(commandName);
        }

        String commandNameWithPrefix = match.toLowerCase() + ":" + commandName;
        if (commandMap.getKnownCommands().get(commandNameWithPrefix) == command) {
            commandMap.getKnownCommands().remove(commandNameWithPrefix);
        }


        for (String alias : command.getAliases()) {
            String aliasName = alias.toLowerCase();
            String aliasNameWithPrefix = match.toLowerCase() + ":" + aliasName;
            if (commandMap.getKnownCommands().get(aliasName) == command) {
                commandMap.getKnownCommands().remove(aliasName);
            }
            if (commandMap.getKnownCommands().get(aliasNameWithPrefix) == command) {
                commandMap.getKnownCommands().remove(aliasNameWithPrefix);
            }
        }
    }

    @Override
    public void registerCommand(String match, Command command) {
        ((CraftServer) Bukkit.getServer()).getCommandMap().register(match, command);
    }

    @Override
    public void listVanish(Player player, boolean shouldEnable) {
        ClientboundPlayerInfoPacket packet = null;

        if (shouldEnable) {
            packet = new ClientboundPlayerInfoPacket(Action.REMOVE_PLAYER, ((CraftPlayer) player).getHandle());
        } else
            packet = new ClientboundPlayerInfoPacket(Action.ADD_PLAYER, ((CraftPlayer) player).getHandle());

        sendToAllButMe(packet, player);
    }

    @Override
    public void sendHoverableJsonMessage(Set<Player> players, String message, String hoverMessage) {
        JsonMessage json = new JsonMessage().append(message).setHoverAsTooltip(hoverMessage).save();
        ClientboundChatPacket packet = new ClientboundChatPacket(Component.Serializer.fromJson(json.getMessage()), ChatType.CHAT, UUID.randomUUID());

        for (Player player : players) {
            ((CraftPlayer) player).getHandle().connection.connection.send(packet);
        }
    }

    private void sendToAllButMe(Packet<?> packet, Player me) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if(!me.getUniqueId().equals(player.getUniqueId())) {
                ((CraftPlayer) player).getHandle().connection.connection.send(packet);
            }
        }
    }

    @Override
    public String getSound(Object object) {
        return null;
    }

    @Override
    public void inject(Player player) {
        final ChannelPipeline pipeline = ((CraftPlayer) player).getHandle().connection.connection.channel.pipeline();
        pipeline.addBefore("packet_handler", player.getUniqueId().toString(), new PacketHandler_v1_18_R0(player));
    }

    @Override
    public void uninject(Player player) {
        final Channel channel = ((CraftPlayer) player).getHandle().connection.connection.channel;
        channel.eventLoop().submit(() -> channel.pipeline().remove(player.getUniqueId().toString()));
    }
}
