package net.shortninja.staffplus.server.compatibility.v1_1x;

import be.garagepoort.staffplusplus.craftbukkit.common.IProtocol;
import be.garagepoort.staffplusplus.craftbukkit.common.json.JsonMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.craftbukkit.v1_19_R2.CraftServer;
import org.bukkit.craftbukkit.v1_19_R2.command.CraftCommandMap;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R2.inventory.CraftItemStack;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

public class Protocol_v1_19_R2 implements IProtocol {
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
        Packet packet = null;
        CraftPlayer craftPlayer = (CraftPlayer) player;
        ServerPlayer sp = craftPlayer.getHandle();

        if (shouldEnable) {
            packet = new ClientboundPlayerInfoRemovePacket(List.of(player.getUniqueId()));
            sendToAllButMe(packet, player);
        } else {
            packet = new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, sp);
            sendToAllButMe(packet, player);
            packet = new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LISTED, sp);
            sendToAllButMe(packet, player);
        }
    }

    @Override
    public void sendHoverableJsonMessage(Set<Player> players, String message, String hoverMessage) {
        JsonMessage json = new JsonMessage().append(message).setHoverAsTooltip(hoverMessage).save();
        ClientboundSystemChatPacket packet = new ClientboundSystemChatPacket(Component.Serializer.fromJson(json.getMessage()), false);

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
        pipeline.addBefore("packet_handler", "staffplusplus_" + player.getUniqueId().toString(), new PacketHandler_v1_19_R2(player));
    }

    @Override
    public void uninject(Player player) {
        final Channel channel = ((CraftPlayer) player).getHandle().connection.connection.channel;
        channel.eventLoop().submit(() -> channel.pipeline().remove(player.getUniqueId().toString()));
    }
}
