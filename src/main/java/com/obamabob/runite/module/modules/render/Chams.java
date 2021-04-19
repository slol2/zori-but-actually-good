package com.obamabob.runite.module.modules.render;

import com.obamabob.runite.Runite;
import com.obamabob.runite.event.Event;
import com.obamabob.runite.event.EventTarget;
import com.obamabob.runite.event.events.EventModelPlayerRender;
import com.obamabob.runite.event.events.EventModelRender;
import com.obamabob.runite.event.events.EventPostRenderLayers;
import com.obamabob.runite.friend.Friends;
import com.obamabob.runite.module.Module;
import com.obamabob.runite.settings.Setting;
import com.obamabob.runite.util.EntityUtil;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

import java.awt.Color;

public class Chams extends Module {

    public static Setting<String> mode;
    public final Setting<Boolean> hand;
    public final Setting<Boolean> lines;
    public final Setting<Float> width;
    public final Setting<Boolean> friendColor;
    public final Setting<Boolean> rainbow;
    public final Setting<Integer> r;
    public final Setting<Integer> g;
    public final Setting<Integer> b;
    public final Setting<Integer> a;
    private static Setting<Boolean> players;
    private static Setting<Boolean> animals;
    private static Setting<Boolean> mobs;
    public static Setting<Boolean> crystals;

    //walls
    public final Setting<Integer> Vr;
    public final Setting<Integer> Vg;
    public final Setting<Integer> Vb;
    public final Setting<Integer> Wr;
    public final Setting<Integer> Wg;
    public final Setting<Integer> Wb;

    public Chams() {
        super("Chams", Category.RENDER);
        mode = register(new Setting<>("Mode", this, "ESP", new String[]{
                "ESP", "Normal", "Walls"
        }));
        Vr = register(new Setting<>("Visible Red", this, 255, 0, 255))
                .visibleWhen(value -> mode.getValue().equalsIgnoreCase("Walls"));
        Vg = register(new Setting<>("Visible Green", this, 0, 0, 255))
                .visibleWhen(value -> mode.getValue().equalsIgnoreCase("Walls"));
        Vb = register(new Setting<>("Visible Blue", this, 0, 0, 255))
                .visibleWhen(value -> mode.getValue().equalsIgnoreCase("Walls"));
        Wr = register(new Setting<>("Wall Red", this, 0, 0, 255))
                .visibleWhen(value -> mode.getValue().equalsIgnoreCase("Walls"));
        Wg = register(new Setting<>("Wall Green", this, 255, 0, 255))
                .visibleWhen(value -> mode.getValue().equalsIgnoreCase("Walls"));
        Wb = register(new Setting<>("Wall Blue", this, 0, 0, 255))
                .visibleWhen(value -> mode.getValue().equalsIgnoreCase("Walls"));
        hand = register(new Setting<>("Hand", this, true))
                .visibleWhen(value -> mode.getValue().equalsIgnoreCase("ESP"));
        lines = register(new Setting<>("Lines", this, false))
                .visibleWhen(value -> mode.getValue().equalsIgnoreCase("ESP"));
        width = register(new Setting<>("Width", this, 1f, 0f, 10f))
                .visibleWhen(value -> mode.getValue().equalsIgnoreCase("ESP"));
        friendColor = register(new Setting<>("Friends", this, true))
                .visibleWhen(value -> mode.getValue().equalsIgnoreCase("ESP"));
        rainbow = register(new Setting<>("Rainbow", this, false))
                .visibleWhen(value -> mode.getValue().equalsIgnoreCase("ESP"));
        r = register(new Setting<>("Red", this, 0, 0, 255))
                .visibleWhen(value -> mode.getValue().equalsIgnoreCase("ESP"));
        g = register(new Setting<>("Green", this, 255, 0, 255))
                .visibleWhen(value -> mode.getValue().equalsIgnoreCase("ESP"));
        b = register(new Setting<>("Blue", this, 255, 0, 255))
                .visibleWhen(value -> mode.getValue().equalsIgnoreCase("ESP"));
        a = register(new Setting<>("Alpha", this, 63, 0, 255))
                .visibleWhen(value -> mode.getValue().equalsIgnoreCase("ESP"));
        players = register(new Setting<>("Players", this, true));
        animals = register(new Setting<>("Animals", this, false));
        mobs = register(new Setting<>("Mobs", this, false));
        crystals = register(new Setting<>("Crystals", this, true))
                .visibleWhen(value -> mode.getValue().equalsIgnoreCase("ESP") || mode.getValue().equalsIgnoreCase("Walls"));
    }

    public static boolean renderChams(Entity entity) {
        return mode.getValue().equalsIgnoreCase("ESP") ? false : (entity instanceof EntityPlayer ? players.getValue() : (EntityUtil.isPassive(entity) ? animals.getValue() : mobs.getValue()));
    }

    @EventTarget
    public void renderPre(EventModelRender event) {
        if (mode.getValue().equalsIgnoreCase("Walls")) {
            if (event.entity instanceof EntityOtherPlayerMP && !players.getValue()) return;
            if (EntityUtil.isPassive(event.entity) && !animals.getValue()) return;
            if (!EntityUtil.isPassive(event.entity) && !mobs.getValue()) return;
            GlStateManager.pushMatrix();
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glColor4f(Wr.getValue() / 255f, Wg.getValue() / 255f, Wb.getValue() / 255f, 1f);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            event.modelBase.render(event.entity, event.limbSwing, event.limbSwingAmount, event.ageInTicks, event.netHeadYaw, event.headPitch, event.scaleFactor);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glColor4f(Vr.getValue() / 255f, Vg.getValue() / 255f, Vb.getValue() / 255f, 1f);
            event.modelBase.render(event.entity, event.limbSwing, event.limbSwingAmount, event.ageInTicks, event.netHeadYaw, event.headPitch, event.scaleFactor);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glColor4f(1f, 1f, 1f, 1f);
            GlStateManager.popMatrix();
            event.setCancelled(true);
        } else if (mode.getValue().equalsIgnoreCase("ESP")) {
            Color c = friendColor.getValue() && Friends.isFriend(event.entity.getName()) ? new Color(0.27f, 0.7f, 0.92f) : rainbow.getValue() ? new Color(Runite.rgb) : new Color(r.getValue(), g.getValue(), b.getValue());
            if (event.getEventState() == Event.State.PRE) {
                if (!(event.entity instanceof EntityOtherPlayerMP)) {
                    if (EntityUtil.isPassive(event.entity) && animals.getValue()) {
                        GL11.glPushMatrix();
                        GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
                        GL11.glPolygonOffset(1f, -100000f);
                        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
                        if (!lines.getValue()) {
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
                        GL11.glColor4f(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, a.getValue() / 255f);
                        if (lines.getValue()) GL11.glLineWidth(width.getValue());
                        event.modelBase.render(event.entity, event.limbSwing, event.limbSwingAmount, event.ageInTicks, event.netHeadYaw, event.headPitch, event.scaleFactor);
                        GL11.glPopAttrib();
                        GL11.glPolygonOffset(1f, 100000f);
                        GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
                        GL11.glPopMatrix();
                        event.setCancelled(true);
                    } else if (!EntityUtil.isPassive(event.entity) && mobs.getValue()) {
                        GL11.glPushMatrix();
                        GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
                        GL11.glPolygonOffset(1f, -100000f);
                        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
                        if (!lines.getValue()) {
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
                        GL11.glColor4f(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, a.getValue() / 255f);
                        if (lines.getValue()) GL11.glLineWidth(width.getValue());
                        event.modelBase.render(event.entity, event.limbSwing, event.limbSwingAmount, event.ageInTicks, event.netHeadYaw, event.headPitch, event.scaleFactor);
                        GL11.glPopAttrib();
                        GL11.glPolygonOffset(1f, 100000f);
                        GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
                        GL11.glPopMatrix();
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventTarget
    public void renderPost(EventPostRenderLayers event) {
        if (mode.getValue().equalsIgnoreCase("ESP")) {
            if (!event.renderer.bindEntityTexture(event.entity)) return;
            Color c = friendColor.getValue() && Friends.isFriend(event.entity.getName()) ? new Color(0.27f, 0.7f, 0.92f) : rainbow.getValue() ? new Color(Runite.rgb) : new Color(r.getValue(), g.getValue(), b.getValue());
            if (event.getEventState() == Event.State.PRE) {
                if (event.entity instanceof EntityOtherPlayerMP && players.getValue()) {
                    GL11.glPushMatrix();
                    GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
                    GL11.glPolygonOffset(1f, -100000f);
                    GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
                    if (!lines.getValue()) {
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
                    GL11.glColor4f(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, a.getValue() / 255f / 2f);
                    if (lines.getValue()) GL11.glLineWidth(width.getValue());
                    event.modelBase.render(event.entity, event.limbSwing, event.limbSwingAmount, event.ageInTicks, event.netHeadYaw, event.headPitch, event.scaleIn);
                    GL11.glPopAttrib();
                    GL11.glPolygonOffset(1f, 100000f);
                    GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
                    GL11.glPopMatrix();
                }
            }
        }
    }

    @EventTarget
    public void onPlayerModel(EventModelPlayerRender event) {
        if (mode.getValue().equalsIgnoreCase("ESP") && players.getValue()) {
            Color c = friendColor.getValue() && Friends.isFriend(event.entity.getName()) ? new Color(0.27f, 0.7f, 0.92f) : rainbow.getValue() ? new Color(Runite.rgb) : new Color(r.getValue(), g.getValue(), b.getValue());
            switch (event.getEventState()) {
                case PRE:
                    GL11.glPushMatrix();
                    GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
                    GL11.glPolygonOffset(1f, -10000000f);
                    GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
                    if (!lines.getValue()) {
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
                    GL11.glColor4f(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, a.getValue() / 255f / 2f);
                    if (lines.getValue()) GL11.glLineWidth(width.getValue());
                    break;
                case POST:
                    GL11.glPopAttrib();
                    GL11.glPolygonOffset(1f, 10000000f);
                    GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
                    GL11.glPopMatrix();
                    break;
            }
        }
    }

}