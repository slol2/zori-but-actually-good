package com.obamabob.runite.mixin.mixins;

import com.obamabob.runite.Runite;
import com.obamabob.runite.module.ModuleManager;
import com.obamabob.runite.module.modules.render.Chams;
import com.obamabob.runite.module.modules.render.Skeleton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(RenderPlayer.class)
public class MixinRenderPlayer {

    @Shadow
    public ModelPlayer getMainModel(){return new ModelPlayer(0f, false);}

    @Redirect(method = "renderRightArm", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelRenderer;render(F)V"))
    private void renderRightArm(ModelRenderer modelRenderer, float scale)  {
        Chams chams = Runite.getInstance().moduleManager.getModuleT(Chams.class);
        Color c = chams.rainbow.getValue() ? new Color(Runite.rgb) : new Color(chams.r.getValue(), chams.g.getValue(), chams.b.getValue());
        if (chams.hand.getValue() && chams.isToggled() && Chams.mode.getValue().equalsIgnoreCase("ESP")) {
            GL11.glPushMatrix();
            GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
            GL11.glPolygonOffset(1f, -10000000f);
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            if (!chams.lines.getValue()) {
                GL11.glPolygonMode(GL11.GL_FRONT, GL11.GL_FILL);
            } else {
                GL11.glPolygonMode(GL11.GL_FRONT, GL11.GL_LINE);
            }
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glColor4f(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, chams.a.getValue() / 255f);
            if (chams.lines.getValue()) GL11.glLineWidth(chams.width.getValue());
        }
        modelRenderer.render(scale);
        if (chams.hand.getValue() && chams.isToggled() && Chams.mode.getValue().equalsIgnoreCase("ESP")) {
            GL11.glPopAttrib();
            GL11.glPolygonOffset(1f, 100000f);
            GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
            GL11.glPopMatrix();
        }
    }

    @Redirect(method = "renderRightArm", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelRenderer;render(F)V", ordinal = 1))
    private void renderRightArmWear(ModelRenderer modelRenderer, float scale)  {
        Chams chams = Runite.getInstance().moduleManager.getModuleT(Chams.class);
        Color c = chams.rainbow.getValue() ? new Color(Runite.rgb) : new Color(chams.r.getValue(), chams.g.getValue(), chams.b.getValue());
        if (chams.hand.getValue() && chams.isToggled() && Chams.mode.getValue().equalsIgnoreCase("ESP")) {
            GL11.glPushMatrix();
            GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
            GL11.glPolygonOffset(1f, -10000000f);
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            if (!chams.lines.getValue()) {
                GL11.glPolygonMode(GL11.GL_FRONT, GL11.GL_FILL);
            } else {
                GL11.glPolygonMode(GL11.GL_FRONT, GL11.GL_LINE);
            }
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glColor4f(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, chams.a.getValue() / 255f);
            if (chams.lines.getValue()) GL11.glLineWidth(chams.width.getValue());
        }
        modelRenderer.render(scale);
        if (chams.hand.getValue() && chams.isToggled() && Chams.mode.getValue().equalsIgnoreCase("ESP")) {
            GL11.glPopAttrib();
            GL11.glPolygonOffset(1f, 100000f);
            GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
            GL11.glPopMatrix();
        }
    }

    @Redirect(method = "renderLeftArm", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelRenderer;render(F)V"))
    private void renderLeftArm(ModelRenderer modelRenderer, float scale)  {
        Chams chams = Runite.getInstance().moduleManager.getModuleT(Chams.class);
        Color c = chams.rainbow.getValue() ? new Color(Runite.rgb) : new Color(chams.r.getValue(), chams.g.getValue(), chams.b.getValue());
        if (chams.hand.getValue() && chams.isToggled() && Chams.mode.getValue().equalsIgnoreCase("ESP")) {
            GL11.glPushMatrix();
            GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
            GL11.glPolygonOffset(1f, -10000000f);
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            if (!chams.lines.getValue()) {
                GL11.glPolygonMode(GL11.GL_FRONT, GL11.GL_FILL);
            } else {
                GL11.glPolygonMode(GL11.GL_FRONT, GL11.GL_LINE);
            }
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glColor4f(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, chams.a.getValue() / 255f);
            if (chams.lines.getValue()) GL11.glLineWidth(chams.width.getValue());
        }
        modelRenderer.render(scale);
        if (chams.hand.getValue() && chams.isToggled() && Chams.mode.getValue().equalsIgnoreCase("ESP")) {
            GL11.glPopAttrib();
            GL11.glPolygonOffset(1f, 100000f);
            GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
            GL11.glPopMatrix();
        }
    }

    @Redirect(method = "renderLeftArm", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelRenderer;render(F)V", ordinal = 1))
    private void renderLeftArmWear(ModelRenderer modelRenderer, float scale)  {
        Chams chams = Runite.getInstance().moduleManager.getModuleT(Chams.class);
        Color c = chams.rainbow.getValue() ? new Color(Runite.rgb) : new Color(chams.r.getValue(), chams.g.getValue(), chams.b.getValue());
        if (chams.hand.getValue() && chams.isToggled() && Chams.mode.getValue().equalsIgnoreCase("ESP")) {
            GL11.glPushMatrix();
            GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
            GL11.glPolygonOffset(1f, -10000000f);
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            if (!chams.lines.getValue()) {
                GL11.glPolygonMode(GL11.GL_FRONT, GL11.GL_FILL);
            } else {
                GL11.glPolygonMode(GL11.GL_FRONT, GL11.GL_LINE);
            }
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glColor4f(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, chams.a.getValue() / 255f);
            if (chams.lines.getValue()) GL11.glLineWidth(chams.width.getValue());
        }
        modelRenderer.render(scale);
        if (chams.hand.getValue() && chams.isToggled() && Chams.mode.getValue().equalsIgnoreCase("ESP")) {
            GL11.glPopAttrib();
            GL11.glPolygonOffset(1f, 100000f);
            GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
            GL11.glPopMatrix();
        }
    }

    @Inject(method = "renderEntityName", at = @At("HEAD"), cancellable = true)
    public void renderEntityNameHook(AbstractClientPlayer entityIn, double x, double y, double z, String name, double distanceSq, CallbackInfo info) {
        if (ModuleManager.isModuleEnabled("Nametags"))
            info.cancel();
    }
}