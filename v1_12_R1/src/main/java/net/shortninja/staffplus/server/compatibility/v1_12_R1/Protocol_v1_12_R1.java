package net.shortninja.staffplus.server.compatibility.v1_12_R1;

import be.garagepoort.staffplusplus.craftbukkit.common.IProtocol;
import be.garagepoort.staffplusplus.craftbukkit.common.json.JsonMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import net.minecraft.server.v1_12_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_12_R1.*;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

public class Protocol_v1_12_R1 implements IProtocol {

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
        SimpleCommandMap commandMap = ((CraftServer) Bukkit.getServer()).getCommandMap();
        command.unregister(commandMap);
        try {
            final Field knownCommands = commandMap.getClass().getDeclaredField("knownCommands");
            knownCommands.setAccessible(true);
            Map<String, Command> cmds = (Map<String, Command>) knownCommands.get(commandMap);

            String commandName = command.getLabel().toLowerCase();
            if (cmds.get(commandName) == command) {
                cmds.remove(commandName);
            }

            String commandNameWithPrefix = match.toLowerCase() + ":" + commandName;
            if (cmds.get(commandNameWithPrefix) == command) {
                cmds.remove(commandNameWithPrefix);
            }

            for (String alias : command.getAliases()) {
                String aliasName = alias.toLowerCase();
                String aliasNameWithPrefix = match.toLowerCase() + ":" + aliasName;
                if (cmds.get(aliasName) == command) {
                    cmds.remove(aliasName);
                }
                if (cmds.get(aliasNameWithPrefix) == command) {
                    cmds.remove(aliasNameWithPrefix);
                }
            }
            knownCommands.set(commandMap, cmds);
        } catch (Exception e) {
            e.printStackTrace();
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

        sendToAllButMe(packet, player);
    }

    @Override
    public void sendHoverableJsonMessage(Set<Player> players, String message, String hoverMessage) {
        JsonMessage json = new JsonMessage().append(message).setHoverAsTooltip(hoverMessage).save();
        PacketPlayOutChat packet = new PacketPlayOutChat(ChatSerializer.a(json.getMessage()));

        for (Player player : players) {
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        }
    }


    private void sendToAllButMe(Packet<?> packet, Player me) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if(!me.getUniqueId().equals(player.getUniqueId())) {
                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
            }
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

    @Override
    public void inject(Player player) {
        final ChannelPipeline pipeline = this.getChannel(player).pipeline();

        // Probably will go wrong at runtime but I have no clue how to fix it. - Ronald.
        //pipeline.addBefore("packet_handler", player.getUniqueId().toString(), new PacketHandler_v1_12_R1(player));
    }

    @Override
    public void uninject(Player player) {
        final Channel channel = this.getChannel(player);
        channel.eventLoop().submit(() -> channel.pipeline().remove(player.getUniqueId().toString()));
    }

    private Channel getChannel(Player player) {
        return ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel;
    }

    private String getSoundName(SoundEffect sound) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        String soundName = "";
        MinecraftKey minecraftKey = getMinecraftKey(sound);

        if (minecraftKey != null) {
            soundName = minecraftKey.b();
        }

        return soundName;
    }

    private MinecraftKey getMinecraftKey(SoundEffect sound) {
        MinecraftKey minecraftKey = null;
        Field field = null;

        try {
            field = SoundEffect.class.getDeclaredField("b");
            field.setAccessible(true);
            minecraftKey = (MinecraftKey) field.get(sound);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException exception) {
            exception.printStackTrace();
        }

        return minecraftKey;
    }
}
