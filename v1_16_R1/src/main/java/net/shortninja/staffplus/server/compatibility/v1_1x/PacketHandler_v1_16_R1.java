package net.shortninja.staffplus.server.compatibility.v1_1x;

import be.garagepoort.staffplusplus.craftbukkit.common.AbstractPacketHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.server.v1_16_R1.PacketPlayOutNamedSoundEffect;
import net.shortninja.staffplus.IStaffPlus;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public final class PacketHandler_v1_16_R1 extends AbstractPacketHandler {

    public PacketHandler_v1_16_R1(Player player) {
        super(player);
    }

    @Override
    public boolean onSend(ChannelHandlerContext context, Object o, ChannelPromise promise) throws Exception {
        if (o instanceof PacketPlayOutNamedSoundEffect) {
            RegisteredServiceProvider<IStaffPlus> provider = Bukkit.getServicesManager().getRegistration(IStaffPlus.class);
            if (provider != null) {
                return !provider.getProvider().isPlayerVanished(player.getUniqueId());
            }
        }

        return true;
    }

    @Override
    public boolean onReceive(ChannelHandlerContext context, Object o) throws Exception {
        return true;
    }
}
