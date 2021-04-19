package com.obamabob.runite.module.modules.render;

import com.obamabob.runite.Runite;
import com.obamabob.runite.event.events.EventRender;
import com.obamabob.runite.friend.Friends;
import com.obamabob.runite.module.Module;
import com.obamabob.runite.settings.Setting;
import com.obamabob.runite.util.RainbowUtil;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.HashMap;

public class Skeleton extends Module {
    private final Setting<String> color = register(new Setting<>("Color Mode", this, "White", new String[]{
            "White", "ClickGui", "Tracers", "Target", "Rainbow"
    }));
    private final Setting<Boolean> friends = register(new Setting<>("Friends", this, true));
    private final Setting<Float> width = register(new Setting<>("Width", this, 1.5f, 0f, 10f));
    private final Setting<Integer> red = register(new Setting<>("Target Red", this, 255, 0, 255));
    private final Setting<Integer> green = register(new Setting<>("Target Green", this, 0, 0, 255));
    private final Setting<Integer> blue = register(new Setting<>("TargetBlue", this, 0, 0, 255));

    public Skeleton() {
        super("Skeleton", Category.RENDER);
    }

    private ICamera camera = new Frustum();
    private static final HashMap<EntityPlayer, float[][]> entities = new HashMap<>();

    private Vec3d getVec3(EventRender event, EntityPlayer e)
    {
        float pt = event.getPartialTicks();
        double x = e.lastTickPosX + (e.posX - e.lastTickPosX) * pt;
        double y = e.lastTickPosY + (e.posY - e.lastTickPosY) * pt;
        double z = e.lastTickPosZ + (e.posZ - e.lastTickPosZ) * pt;
        return new Vec3d(x, y, z);
    }

    private void glColor(EntityPlayer entity) {
        if (Friends.isFriend(entity.getName()) && friends.getValue()) {
            GlStateManager.color(0.27f, 0.7f, 0.92f, 1f);
            return;
        }
        if (color.getValue().equalsIgnoreCase("ClickGui")) {
            Color temp = new Color(Runite.rgb);
            GlStateManager.color(temp.getRed() / 255f, temp.getGreen() / 255f, temp.getBlue() / 255f, 1f);
        } else if (color.getValue().equalsIgnoreCase("Tracers")) {
            final float distance = mc.player.getDistance(entity);
            if (distance <= 32.0f) {
                GlStateManager.color(1.0f - ((distance / 32.0f) / 2), distance / 32.0f, 0.0f, 1f);
            }
            else {
                GlStateManager.color(0.0f, 0.9f, 0.0f, 1f);
            }
        } else if (color.getValue().equalsIgnoreCase("Target")) {
            Color temp = new Color(Runite.rgb);
            GlStateManager.color(temp.getRed() / 255f, temp.getGreen() / 255f, temp.getBlue() / 255f, 1f);
        } else if (color.getValue().equalsIgnoreCase("Rainbow")) {
            Color c = new Color(Runite.rgb);
            GlStateManager.color(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, c.getAlpha() / 255f);
        } else {
            GlStateManager.color(1f, 1f, 1f, 1f);
        }
    }

    @Override
    public void onWorldRender(EventRender event) {
        if (mc.getRenderManager() == null || mc.getRenderManager().options == null)
            return;

        startEnd(true);
        GL11.glEnable(2903);
        GL11.glDisable(2848);
        entities.keySet().removeIf(this::doesntContain);

        mc.world.playerEntities.forEach(e -> drawSkeleton(event, e));

        Gui.drawRect(0, 0, 0, 0, 0);
        startEnd(false);
    }

    private void drawSkeleton(EventRender event, EntityPlayer e)
    {
        double d3 = mc.player.lastTickPosX + (mc.player.posX - mc.player.lastTickPosX) * (double)event.getPartialTicks();
        double d4 = mc.player.lastTickPosY + (mc.player.posY - mc.player.lastTickPosY) * (double)event.getPartialTicks();
        double d5 = mc.player.lastTickPosZ + (mc.player.posZ - mc.player.lastTickPosZ) * (double)event.getPartialTicks();

        camera.setPosition(d3,  d4,  d5);

        float[][] entPos = entities.get(e);
        if (entPos != null && e.isEntityAlive() && camera.isBoundingBoxInFrustum(e.getEntityBoundingBox()) && !e.isDead && e != mc.player && !e.isPlayerSleeping())
        {
            GL11.glPushMatrix();
            GL11.glEnable(2848);
            GL11.glLineWidth(width.getValue());
            //GlStateManager.color(255 / 255.0F, 255 / 255.0F, 255 / 255.0F, 1.0F);
            glColor(e);
            Vec3d vec = getVec3(event, e);
            double x = vec.x - mc.getRenderManager().renderPosX;
            double y = vec.y - mc.getRenderManager().renderPosY;
            double z = vec.z - mc.getRenderManager().renderPosZ;
            GL11.glTranslated(x, y, z);
            float xOff = e.prevRenderYawOffset + (e.renderYawOffset - e.prevRenderYawOffset) * event.getPartialTicks();
            GL11.glRotatef(-xOff, 0.0F, 1.0F, 0.0F);
            GL11.glTranslated(0.0D, 0.0D, e.isSneaking() ? -0.235D : 0.0D);
            float yOff = e.isSneaking() ? 0.6F : 0.75F;
            GL11.glPushMatrix();
            //GlStateManager.color(1f, 1f, 1f, 1.0F);
            glColor(e);
            GL11.glTranslated(-0.125D, yOff, 0.0D);
            if (entPos[3][0] != 0.0F)
                GL11.glRotatef(entPos[3][0] * 57.295776F, 1.0F, 0.0F, 0.0F);
            if (entPos[3][1] != 0.0F)
                GL11.glRotatef(entPos[3][1] * 57.295776F, 0.0F, 1.0F, 0.0F);
            if (entPos[3][2] != 0.0F)
                GL11.glRotatef(entPos[3][2] * 57.295776F, 0.0F, 0.0F, 1.0F);
            GL11.glBegin(3);
            GL11.glVertex3d(0.0D, 0.0D, 0.0D);
            GL11.glVertex3d(0.0D, -yOff, 0.0D);
            GL11.glEnd();
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            //GlStateManager.color(1f, 1f, 1f, 1.0F);
            glColor(e);
            GL11.glTranslated(0.125D, yOff, 0.0D);
            if (entPos[4][0] != 0.0F)
                GL11.glRotatef(entPos[4][0] * 57.295776F, 1.0F, 0.0F, 0.0F);
            if (entPos[4][1] != 0.0F)
                GL11.glRotatef(entPos[4][1] * 57.295776F, 0.0F, 1.0F, 0.0F);
            if (entPos[4][2] != 0.0F)
                GL11.glRotatef(entPos[4][2] * 57.295776F, 0.0F, 0.0F, 1.0F);
            GL11.glBegin(3);
            GL11.glVertex3d(0.0D, 0.0D, 0.0D);
            GL11.glVertex3d(0.0D, -yOff, 0.0D);
            GL11.glEnd();
            GL11.glPopMatrix();
            GL11.glTranslated(0.0D, 0.0D, e.isSneaking() ? 0.25D : 0.0D);
            GL11.glPushMatrix();
            //GlStateManager.color(1f, 1f, 1f, 1.0F);
            glColor(e);
            GL11.glTranslated(0.0D, e.isSneaking() ? -0.05D : 0.0D, e.isSneaking() ? -0.01725D : 0.0D);
            GL11.glPushMatrix();
            //GlStateManager.color(1f, 1f, 1f, 1.0F);
            glColor(e);
            GL11.glTranslated(-0.375D, yOff + 0.55D, 0.0D);
            if (entPos[1][0] != 0.0F)
                GL11.glRotatef(entPos[1][0] * 57.295776F, 1.0F, 0.0F, 0.0F);
            if (entPos[1][1] != 0.0F)
                GL11.glRotatef(entPos[1][1] * 57.295776F, 0.0F, 1.0F, 0.0F);
            if (entPos[1][2] != 0.0F)
                GL11.glRotatef(-entPos[1][2] * 57.295776F, 0.0F, 0.0F, 1.0F);
            GL11.glBegin(3);
            GL11.glVertex3d(0.0D, 0.0D, 0.0D);
            GL11.glVertex3d(0.0D, -0.5D, 0.0D);
            GL11.glEnd();
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glTranslated(0.375D, yOff + 0.55D, 0.0D);
            if (entPos[2][0] != 0.0F)
                GL11.glRotatef(entPos[2][0] * 57.295776F, 1.0F, 0.0F, 0.0F);
            if (entPos[2][1] != 0.0F)
                GL11.glRotatef(entPos[2][1] * 57.295776F, 0.0F, 1.0F, 0.0F);
            if (entPos[2][2] != 0.0F)
                GL11.glRotatef(-entPos[2][2] * 57.295776F, 0.0F, 0.0F, 1.0F);
            GL11.glBegin(3);
            GL11.glVertex3d(0.0D, 0.0D, 0.0D);
            GL11.glVertex3d(0.0D, -0.5D, 0.0D);
            GL11.glEnd();
            GL11.glPopMatrix();
            GL11.glRotatef(xOff - e.rotationYawHead, 0.0F, 1.0F, 0.0F);
            GL11.glPushMatrix();
            //GlStateManager.color(1f, 1f, 1f, 1.0F);
            glColor(e);
            GL11.glTranslated(0.0D, yOff + 0.55D, 0.0D);
            if (entPos[0][0] != 0.0F)
                GL11.glRotatef(entPos[0][0] * 57.295776F, 1.0F, 0.0F, 0.0F);
            GL11.glBegin(3);
            GL11.glVertex3d(0.0D, 0.0D, 0.0D);
            GL11.glVertex3d(0.0D, 0.3D, 0.0D);
            GL11.glEnd();
            GL11.glPopMatrix();
            GL11.glPopMatrix();
            GL11.glRotatef(e.isSneaking() ? 25.0F : 0.0F, 1.0F, 0.0F, 0.0F);
            GL11.glTranslated(0.0D, e.isSneaking() ? -0.16175D : 0.0D, e.isSneaking() ? -0.48025D : 0.0D);
            GL11.glPushMatrix();
            GL11.glTranslated(0.0D, yOff, 0.0D);
            GL11.glBegin(3);
            GL11.glVertex3d(-0.125D, 0.0D, 0.0D);
            GL11.glVertex3d(0.125D, 0.0D, 0.0D);
            GL11.glEnd();
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            //GlStateManager.color(1f, 1f, 1f, 1.0F);
            glColor(e);
            GL11.glTranslated(0.0D, yOff, 0.0D);
            GL11.glBegin(3);
            GL11.glVertex3d(0.0D, 0.0D, 0.0D);
            GL11.glVertex3d(0.0D, 0.55D, 0.0D);
            GL11.glEnd();
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glTranslated(0.0D, yOff + 0.55D, 0.0D);
            GL11.glBegin(3);
            GL11.glVertex3d(-0.375D, 0.0D, 0.0D);
            GL11.glVertex3d(0.375D, 0.0D, 0.0D);
            GL11.glEnd();
            GL11.glPopMatrix();
            GL11.glPopMatrix();
        }
    }

    private void startEnd(boolean revert) {
        if (revert) {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GL11.glEnable(2848);
            GlStateManager.disableDepth();
            GlStateManager.disableTexture2D();
            GL11.glHint(3154, 4354);
        } else {
            GlStateManager.disableBlend();
            GlStateManager.enableTexture2D();
            GL11.glDisable(2848);
            GlStateManager.enableDepth();
            GlStateManager.popMatrix();
        }
        GlStateManager.depthMask(!revert);
    }

    public static void addEntity(EntityPlayer e, ModelPlayer model) {
        entities.put(e, new float[][] { { model.bipedHead.rotateAngleX, model.bipedHead.rotateAngleY, model.bipedHead.rotateAngleZ }, { model.bipedRightArm.rotateAngleX, model.bipedRightArm.rotateAngleY, model.bipedRightArm.rotateAngleZ }, { model.bipedLeftLeg.rotateAngleX, model.bipedLeftLeg.rotateAngleY, model.bipedLeftLeg.rotateAngleZ }, { model.bipedRightLeg.rotateAngleX, model.bipedRightLeg.rotateAngleY, model.bipedRightLeg.rotateAngleZ }, { model.bipedLeftLeg.rotateAngleX, model.bipedLeftLeg.rotateAngleY, model.bipedLeftLeg.rotateAngleZ } });
    }


    private boolean doesntContain(EntityPlayer var0) {
        return !mc.world.playerEntities.contains(var0);
    }
}
