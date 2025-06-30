package net.shortninja.staffplus.server.compatibility.v1_1x;

import be.garagepoort.staffplusplus.craftbukkit.common.IProtocol;
import be.garagepoort.staffplusplus.craftbukkit.common.json.JsonMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
import org.bukkit.craftbukkit.v1_21_R5.CraftWorld;
import org.bukkit.craftbukkit.v1_21_R5.command.CraftCommandMap;
import org.bukkit.craftbukkit.v1_21_R5.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_21_R5.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_21_R5.util.CraftChatMessage;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

public class Protocol_v1_21_R4 implements IProtocol {
    private static final ServerLevel SERVER_LEVEL = ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle(); // The overworld
    
    @Override
    public org.bukkit.inventory.ItemStack addNbtString(org.bukkit.inventory.ItemStack item, String value) {
        ItemStack craftItem = CraftItemStack.asNMSCopy(item);
        CustomData defaultData = CustomData.of(new CompoundTag());
        
        CustomData customData = craftItem.getComponents().getOrDefault(DataComponents.CUSTOM_DATA, defaultData);
        CompoundTag nbtCompound = customData.copyTag();
        
        nbtCompound.putString(NBT_IDENTIFIER, value);
        CustomData.set(DataComponents.CUSTOM_DATA, craftItem, nbtCompound);

        return CraftItemStack.asCraftMirror(craftItem);
    }

    @Override
    public String getNbtString(org.bukkit.inventory.ItemStack item) {
        ItemStack craftItem = CraftItemStack.asNMSCopy(item);
        if (craftItem == null) {
            return "";
        }
        
        CustomData defaultData = CustomData.of(new CompoundTag());
        CustomData customData = craftItem.getComponents().getOrDefault(DataComponents.CUSTOM_DATA, defaultData);
        
        return customData.getUnsafe().getString(NBT_IDENTIFIER).orElse("");
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
        ClientboundSystemChatPacket packet = new ClientboundSystemChatPacket(CraftChatMessage.fromJSON(json.getMessage()), false);
        for (Player player : players) {
            Connection o = getConnection(player);
            o.send(packet);
        }
    }

    private static Connection getConnection(Player player) {
        try {
            ServerGamePacketListenerImpl serverGamePacketListener = ((CraftPlayer) player).getHandle().connection;
            
            Field connectionField = ServerGamePacketListenerImpl.class.getSuperclass().getDeclaredField("e"); // connection
            connectionField.setAccessible(true);
            Connection connection = (Connection) connectionField.get(serverGamePacketListener);
            
            return connection;
            
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException("Failed to get " + player.getName() + "'s connection object", e);
        }
    }
    
    private static Channel getChannel(Connection connection) {
        try {
            Field channelField = Connection.class.getDeclaredField("n"); // channel
            channelField.setAccessible(true);
            return (Channel) channelField.get(connection);
            
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException("Failed to get connection channel", e);
        }
    }

    private void sendToAllButMe(Packet<?> packet, Player me) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!me.getUniqueId().equals(player.getUniqueId())) {
                Connection o = getConnection(player);
                o.send(packet);
            }
        }
    }

    @Override
    public String getSound(Object object) {
        return null;
    }

    @Override
    public void inject(Player player) {
        final Connection connection = getConnection(player);
        final ChannelPipeline pipeline = getChannel(connection).pipeline();
        
        pipeline.addBefore("packet_handler", "staffplusplus_" + player.getUniqueId(), new PacketHandler_v1_21_R4(player));
    }

    @Override
    public void uninject(Player player) {
        final Connection connection = getConnection(player);
        final Channel channel = getChannel(connection);
        
        channel.eventLoop().submit(() -> channel.pipeline().remove(player.getUniqueId().toString()));
    }

}
