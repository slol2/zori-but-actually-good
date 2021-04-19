package com.obamabob.runite.mixin.mixins;

import com.obamabob.runite.event.Event;
import com.obamabob.runite.event.events.EventModelPlayerRender;
import com.obamabob.runite.module.modules.render.Skeleton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ModelPlayer.class, priority = 9999)
public class MixinModelPlayer {

    @Shadow
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {}

    @Inject(method = "setRotationAngles", at = @At("RETURN"))
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn, CallbackInfo callbackInfo) {
        if (Minecraft.getMinecraft().world != null && Minecraft.getMinecraft().player != null && entityIn instanceof EntityPlayer) {
            Skeleton.addEntity((EntityPlayer)entityIn, (ModelPlayer) (Object) this);
        }
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void renderPre(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale, CallbackInfo ci) {
        EventModelPlayerRender modelrenderpre = new EventModelPlayerRender(ModelPlayer.class.cast(this), entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        modelrenderpre.setState(Event.State.PRE);
        modelrenderpre.call();
        if (modelrenderpre.isCancelled()) ci.cancel();
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void renderPost(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale, CallbackInfo ci) {
        EventModelPlayerRender modelrenderpost = new EventModelPlayerRender(ModelPlayer.class.cast(this), entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        modelrenderpost.setState(Event.State.POST);
        modelrenderpost.call();
    }
}
