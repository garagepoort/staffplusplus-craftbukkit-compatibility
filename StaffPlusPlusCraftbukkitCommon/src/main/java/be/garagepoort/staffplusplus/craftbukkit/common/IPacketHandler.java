package be.garagepoort.staffplusplus.craftbukkit.common;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

public interface IPacketHandler {

    boolean onSend(ChannelHandlerContext context, Object o, ChannelPromise promise) throws Exception;

    boolean onReceive(ChannelHandlerContext context, Object o) throws Exception;
}
