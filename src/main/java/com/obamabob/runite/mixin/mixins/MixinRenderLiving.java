package com.obamabob.runite.mixin.mixins;

import com.obamabob.runite.module.ModuleManager;
import com.obamabob.runite.module.modules.render.Chams;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderLiving.class)
public class MixinRenderLiving<T extends Entity> {
    @Inject(method = "doRender", at = @At("HEAD"))
    private void injectChamsPre(EntityLiving entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo info) {
        if (ModuleManager.getModuleByName("Chams").isToggled() && Chams.renderChams(entity)) {
            GL11.glEnable(32823);
            GL11.glPolygonOffset(1.0f, -1000000.0f);
        }
    }

    @Inject(method = "doRender", at = @At("RETURN"))
    private <S extends EntityLivingBase> void injectChamsPost(EntityLiving entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo info) {
        if (ModuleManager.isModuleEnabled("Chams") && Chams.renderChams(entity)) {
            GL11.glPolygonOffset(1.0f, 1000000.0f);
            GL11.glDisable(32823);
        }
    }
}