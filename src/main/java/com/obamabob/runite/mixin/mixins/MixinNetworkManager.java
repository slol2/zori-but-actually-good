package com.obamabob.runite.mixin.mixins;

import com.obamabob.runite.Runite;
import com.obamabob.runite.event.events.EventPacket;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetworkManager.class)
public class MixinNetworkManager {
    @Inject(method = "channelRead0", at = @At("HEAD"), cancellable = true)
    public void IchannelRead0(ChannelHandlerContext context, Packet<?> packet, CallbackInfo callback) {
        EventPacket.Receive event = new EventPacket.Receive(packet);
        event.call();
        if (event.isCancelled()) {
            callback.cancel();
        }
    }

    @Inject(method = "sendPacket(Lnet/minecraft/network/Packet;)V", at = @At("HEAD"), cancellable = true)
    public void IsendPacket(Packet<?> packet, CallbackInfo callback) {
        EventPacket.Send event = new EventPacket.Send(packet);
        event.call();
        if (event.isCancelled()) {
            callback.cancel();
        }
    }
}