package net.shortninja.staffplus.server.compatibility.v1_1x;

import be.garagepoort.staffplusplus.craftbukkit.common.IProtocol;
import be.garagepoort.staffplusplus.craftbukkit.common.json.JsonMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import net.minecraft.server.v1_16_R2.*;
import net.minecraft.server.v1_16_R2.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_16_R2.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.craftbukkit.v1_16_R2.CraftServer;
import org.bukkit.craftbukkit.v1_16_R2.command.CraftCommandMap;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R2.inventory.CraftItemStack;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.Set;
import java.util.UUID;

public class Protocol_v1_16_R2 implements IProtocol {
    @Override
    public org.bukkit.inventory.ItemStack addNbtString(org.bukkit.inventory.ItemStack item, String value) {
        ItemStack craftItem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound nbtCompound = craftItem.getTag() == null ? new NBTTagCompound() : craftItem.getTag();

        nbtCompound.setString(NBT_IDENTIFIER, value);
        craftItem.setTag(nbtCompound);

        return CraftItemStack.asCraftMirror(craftItem);
    }

    @Override
    public String getNbtString(org.bukkit.inventory.ItemStack item) {
        ItemStack craftItem = CraftItemStack.asNMSCopy(item);

        if (craftItem == null) {
            return "";
        }

        NBTTagCompound nbtCompound = craftItem.getTag() == null ? new NBTTagCompound() : craftItem.getTag();

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
        PacketPlayOutPlayerInfo packet = null;

        if (shouldEnable) {
            packet = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER, ((CraftPlayer) player).getHandle());
        } else
            packet = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, ((CraftPlayer) player).getHandle());

        sendGlobalPacket(packet);
    }

    @Override
    public void sendHoverableJsonMessage(Set<Player> players, String message, String hoverMessage) {
        JsonMessage json = new JsonMessage().append(message).setHoverAsTooltip(hoverMessage).save();
        PacketPlayOutChat packet = new PacketPlayOutChat(ChatSerializer.a(json.getMessage()), ChatMessageType.CHAT, UUID.fromString(""));

        for (Player player : players) {
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        }
    }


    private void sendGlobalPacket(Packet<?> packet) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        }
    }

    @Override
    public String getSound(Object object) {
        try {
            return object instanceof SoundEffect ? getSoundName((SoundEffect) object) : null;
        } catch (Exception e) {
            return null;
        }
    }

    private String getSoundName(SoundEffect sound) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        String soundName = "";
        MinecraftKey minecraftKey = getMinecraftKey(sound);

        if (minecraftKey != null) {
            soundName = minecraftKey.getNamespace();

        }

        return soundName;
    }

    private MinecraftKey getMinecraftKey(SoundEffect sound) {
        MinecraftKey minecraftKey = null;
        Field field = null;

        try {
            field = SoundEffect.class.getDeclaredField("a");
            field.setAccessible(true);
            minecraftKey = (MinecraftKey) field.get(sound);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException exception) {
            exception.printStackTrace();
        }

        return minecraftKey;
    }

    @Override
    public void inject(Player player) {
        final ChannelPipeline pipeline = ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel.pipeline();
        pipeline.addBefore("packet_handler", player.getUniqueId().toString(), new PacketHandler_v1_16_R2(player));
    }

    @Override
    public void uninject(Player player) {
        final Channel channel = ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel;
        channel.eventLoop().submit(() -> channel.pipeline().remove(player.getUniqueId().toString()));
    }
}