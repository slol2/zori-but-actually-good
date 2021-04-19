package com.obamabob.runite.module.modules.render;

import com.obamabob.runite.Runite;
import com.obamabob.runite.event.events.EventRender;
import com.obamabob.runite.friend.Friends;
import com.obamabob.runite.module.Module;
import com.obamabob.runite.settings.Setting;
import com.obamabob.runite.util.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.gui.FontRenderer;

import java.awt.*;
import java.util.*;

public class Nametags extends Module {

    public Nametags() {
        super("Nametags", Category.RENDER);
    }

    private final Setting<Boolean> armor = register(new Setting<>("Armor", this, true));
    private final Setting<Boolean> reversed = register(new Setting<>("ArmorReversed", this, true));
    private final Setting<Boolean> health = register(new Setting<>("Health",this, true));
    private final Setting<Boolean> ping = register(new Setting<>("Ping",this, true));
    private final Setting<Boolean> gamemode = register(new Setting<>("Gamemode",this, false));
    private final Setting<Boolean> entityID = register(new Setting<>("EntityID",this, false));
    private final Setting<Boolean> heldStackName = register(new Setting<>("StackName",this, true));

    private final Setting<Boolean> max = register(new Setting<>("Max", this,true));
    private final Setting<Boolean> maxText = register(new Setting<>("NoMaxText", this,false));

    private final Setting<Float> size = register(new Setting<>("Size",this, 2.0f, 0.1f, 20.0f));
    private final Setting<Boolean> scaleing = register(new Setting<>("Scale",this, false));
    private final Setting<Boolean> smartScale = register(new Setting<>("SmartScale", this,false));
    private final Setting<Float> factor = register(new Setting<>("Factor", this,0.3f, 0.1f, 1.0f));

    private final Setting<Boolean> NCRainbow = register(new Setting<>("Text-Rainbow", this,false));
    private final Setting<Integer> NCred = register(new Setting<>("Text-Red", this,255, 0, 255));
    private final Setting<Integer> NCgreen = register(new Setting<>("Text-Green", this,255, 0, 255));
    private final Setting<Integer> NCblue = register(new Setting<>("Text-Blue", this,255, 0, 255));

    private final Setting<Boolean> FCRainbow = register(new Setting<>("Friend-Rainbow",this, false));
    private final Setting<Integer> FCred = register(new Setting<>("Friend-Red",this, 0, 0, 255));
    private final Setting<Integer> FCgreen = register(new Setting<>("Friend-Green", this,213, 0, 255));
    private final Setting<Integer> FCblue = register(new Setting<>("Friend-Blue",this, 255, 0, 255));

    private final Setting<Boolean> sneak = register(new Setting<>("Sneak",this, true));
    private final Setting<Boolean> SCRainbow = register(new Setting<>("Sneak-Rainbow", this,false));
    private final Setting<Integer> SCred = register(new Setting<>("Sneak-Red", this,245, 0, 255));
    private final Setting<Integer> SCgreen = register(new Setting<>("Sneak-Green", this,0, 0, 255));
    private final Setting<Integer> SCblue = register(new Setting<>("Text-Blue", this,122, 0, 255));

    private final Setting<Boolean> invisibles = register(new Setting<>("Invisibles",this, true));
    private final Setting<Boolean> ICRainbow = register(new Setting<>("1-Rainbow",this, false));
    private final Setting<Integer> ICred = register(new Setting<>("Invisible-Red",this, 148, 0, 255));
    private final Setting<Integer> ICgreen = register(new Setting<>("Invisible-Green",this, 148, 0, 255));
    private final Setting<Integer> ICblue = register(new Setting<>("Invisible-Blue",this, 148, 0, 255));

    private final Setting<Boolean> outline = register(new Setting<>("Outline", this, true));
    private final Setting<Float> Owidth = register(new Setting<>("Outline-Width",this, 1.5f, 0f, 3f));
    private final Setting<Boolean> ORainbow = register(new Setting<>("Outline-Rainbow", this, true));
    private final Setting<Integer> Ored = register(new Setting<>("Outline-Red", this, 255, 0, 255));
    private final Setting<Integer> Ogreen = register(new Setting<>("Outline-Green",this, 255, 0, 255));
    private final Setting<Integer> Oblue = register(new Setting<>("Outline-Blue", this,255, 0, 255));
    private final Setting<Boolean> FORainbow = register(new Setting<>("FriendOutline-Rainbow", this,false));
    private final Setting<Integer> FOred = register(new Setting<>("FriendOutline-Red", this,0, 0, 255));
    private final Setting<Integer> FOgreen = register(new Setting<>("FriendOutline-Green", this,213, 0, 255));
    private final Setting<Integer> FOblue = register(new Setting<>("FriendOutline-Blue", this,255, 0, 255));
    private final Setting<Boolean> IORainbow = register(new Setting<>("InvisibleOutline-Rainbow", this,false));
    private final Setting<Integer> IOred = register(new Setting<>("InvisibleOutline-Red", this,148, 0, 255));
    private final Setting<Integer> IOgreen = register(new Setting<>("InvisibleOutline-Green", this,148, 0, 255));
    private final Setting<Integer> IOblue = register(new Setting<>("InvisibleOutline-Blue", this,148, 0, 255));
    private final Setting<Boolean> SORainbow = register(new Setting<>("SneakOutline-Rainbow", this,false));
    private final Setting<Integer> SOred = register(new Setting<>("SneakOutline-Red", this,245, 0, 255));
    private final Setting<Integer> SOgreen = register(new Setting<>("SneakOutline-Green", this,0, 0, 255));
    private final Setting<Integer> SOblue = register(new Setting<>("SneakOutline-Blue", this,122, 0, 255));

    private static Nametags INSTANCE = new Nametags();

    public static Nametags getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Nametags();
        }
        return INSTANCE;
    }

    public void onWorldRender(EventRender event) {
        for (EntityPlayer player : mc.world.playerEntities) {
            if (player != null && !player.equals(mc.player) && player.isEntityAlive() && (!player.isInvisible() || invisibles.getValue())) {
                double x = interpolate(player.lastTickPosX, player.posX, event.getPartialTicks()) - mc.getRenderManager().renderPosX;
                double y = interpolate(player.lastTickPosY, player.posY, event.getPartialTicks()) - mc.getRenderManager().renderPosY;
                double z = interpolate(player.lastTickPosZ, player.posZ, event.getPartialTicks()) - mc.getRenderManager().renderPosZ;
                renderNameTag(player, x, y, z, event.getPartialTicks());
            }
        }
    }

    private void renderNameTag(EntityPlayer player, double x, double y, double z, float delta) {
        double tempY = y;
        tempY += (player.isSneaking() ? 0.5D : 0.7D);
        Entity camera = mc.getRenderViewEntity();
        assert camera != null;
        double originalPositionX = camera.posX;
        double originalPositionY = camera.posY;
        double originalPositionZ = camera.posZ;
        camera.posX = interpolate(camera.prevPosX, camera.posX, delta);
        camera.posY = interpolate(camera.prevPosY, camera.posY, delta);
        camera.posZ = interpolate(camera.prevPosZ, camera.posZ, delta);

        String displayTag = getDisplayTag(player);
        double distance = camera.getDistance(x + mc.getRenderManager().viewerPosX, y + mc.getRenderManager().viewerPosY, z + mc.getRenderManager().viewerPosZ);
        int width = mc.fontRenderer.getStringWidth(displayTag) / 2;
        double scale = (0.0018 + size.getValue() * (distance * factor.getValue())) / 1000.0;

        if (distance <= 8 && smartScale.getValue()) {
            scale = 0.0245D;
        }

        if (!scaleing.getValue()) {
            scale = size.getValue() / 100.0;
        }

        GlStateManager.pushMatrix();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enablePolygonOffset();
        GlStateManager.doPolygonOffset(1, -1500000);
        GlStateManager.disableLighting();
        GlStateManager.translate((float) x, (float) tempY + 1.4F, (float) z);
        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(mc.getRenderManager().playerViewX, mc.gameSettings.thirdPersonView == 2 ? -1.0F : 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(-scale, -scale, scale);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.enableBlend();
        if (outline.getValue()) {
            drawBorderedRectReliant((float) (-width - 1), (float) (-mc.fontRenderer.FONT_HEIGHT), (float) (width), 1.0f, (float)Owidth.getValue(), 1426064384, this.getOutlineColor(player));
        } else {
            drawBorderedRectReliant((float) (-width - 1), (float) (-mc.fontRenderer.FONT_HEIGHT), (float) (width), 1.0f, 1.8f, 1426064384, 855638016);
        }

        GlStateManager.disableBlend();

        ItemStack renderMainHand = player.getHeldItemMainhand().copy();
        if (renderMainHand.hasEffect() && (renderMainHand.getItem() instanceof ItemTool || renderMainHand.getItem() instanceof ItemArmor)) {
            renderMainHand.stackSize = 1;
        }

        if (heldStackName.getValue() && !renderMainHand.isEmpty() && renderMainHand.getItem() != Items.AIR) {
            String stackName = renderMainHand.getDisplayName();
            int stackNameWidth = mc.fontRenderer.getStringWidth(stackName) / 2;
            GL11.glPushMatrix();
            GL11.glScalef(0.75f, 0.75f, 0);
            mc.fontRenderer.drawStringWithShadow(stackName, -stackNameWidth, -(getBiggestArmorTag(player) + 20), 0xFFFFFFFF);
            GL11.glScalef(1.5f, 1.5f, 1);
            GL11.glPopMatrix();
        }

        if (armor.getValue()) {
            GlStateManager.pushMatrix();
            int xOffset = -6;
            int count = 0;
            for (ItemStack armourStack : player.inventory.armorInventory) {
                if (armourStack != null) {
                    xOffset -= 8;
                    if (armourStack.getItem() != Items.AIR) ++count;
                }
            }

            xOffset -= 8;
            ItemStack renderOffhand = player.getHeldItemOffhand().copy();
            if (renderOffhand.hasEffect() && (renderOffhand.getItem() instanceof ItemTool || renderOffhand.getItem() instanceof ItemArmor)) {
                renderOffhand.stackSize = 1;
            }

            this.renderItemStack(renderOffhand, xOffset, -26);
            xOffset += 16;

            if (reversed.getValue()) {
                for (int index = 0; index <= 3; ++index) {
                    ItemStack armourStack = player.inventory.armorInventory.get(index);
                    if (armourStack != null && armourStack.getItem() != Items.AIR) {
                        ItemStack renderStack1 = armourStack.copy();

                        this.renderItemStack(armourStack, xOffset, -26);
                        xOffset += 16;
                    }
                }
            } else {
                for (int index = 3; index >= 0; --index) {
                    ItemStack armourStack = player.inventory.armorInventory.get(index);
                    if (armourStack != null && armourStack.getItem() != Items.AIR) {
                        ItemStack renderStack1 = armourStack.copy();

                        this.renderItemStack(armourStack, xOffset, -26);
                        xOffset += 16;
                    }
                }
            }

            this.renderItemStack(renderMainHand, xOffset, -26);

            GlStateManager.popMatrix();
        }

        mc.fontRenderer.drawStringWithShadow(displayTag, -width, -(mc.fontRenderer.FONT_HEIGHT - 1), this.getDisplayColor(player));

        camera.posX = originalPositionX;
        camera.posY = originalPositionY;
        camera.posZ = originalPositionZ;
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.disablePolygonOffset();
        GlStateManager.doPolygonOffset(1, 1500000);
        GlStateManager.popMatrix();
    }

    private int getDisplayColor(EntityPlayer player) {
        int displaycolor = ColorHolder.toHex(NCred.getValue(), NCgreen.getValue(), NCblue.getValue());
        if (Friends.isFriend(player.getName())) {
            return ColorHolder.toHex(FCred.getValue(), FCgreen.getValue(), FCblue.getValue());
        } else if (player.isInvisible() && invisibles.getValue()) {
            displaycolor = ColorHolder.toHex(ICred.getValue(), ICgreen.getValue(), ICblue.getValue());
        } else if (player.isSneaking() && sneak.getValue()) {
            displaycolor = ColorHolder.toHex(SCred.getValue(), SCgreen.getValue(), SCblue.getValue());
        }
        return displaycolor;
    }

    private int getOutlineColor(EntityPlayer player) {
        int outlinecolor = ColorHolder.toHex(Ored.getValue(), Ogreen.getValue(), Oblue.getValue());
        if (Friends.isFriend(player.getName())) {
            outlinecolor = ColorHolder.toHex(FOred.getValue(), FOgreen.getValue(), FOblue.getValue());
        } else if (player.isInvisible() && invisibles.getValue()) {
            outlinecolor = ColorHolder.toHex(IOred.getValue(), IOgreen.getValue(), IOblue.getValue());
        } else if (player.isSneaking() && sneak.getValue()) {
            outlinecolor = ColorHolder.toHex(SOred.getValue(), SOgreen.getValue(), SOblue.getValue());
        }
        return outlinecolor;
    }

    private void renderItemStack(ItemStack stack, int x, int y) {
        GlStateManager.pushMatrix();
        GlStateManager.depthMask(true);
        GlStateManager.clear(GL11.GL_ACCUM);

        RenderHelper.enableStandardItemLighting();
        mc.getRenderItem().zLevel = -150.0F;
        GlStateManager.disableAlpha();
        GlStateManager.enableDepth();
        GlStateManager.disableCull();

        mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, y);
        mc.getRenderItem().renderItemOverlays(mc.fontRenderer, stack, x, y);

        mc.getRenderItem().zLevel = 0.0F;
        RenderHelper.disableStandardItemLighting();

        GlStateManager.enableCull();
        GlStateManager.enableAlpha();

        GlStateManager.scale(0.5F, 0.5F, 0.5F);
        GlStateManager.disableDepth();
        renderEnchantmentText(stack, x, y);
        GlStateManager.enableDepth();
        GlStateManager.scale(2F, 2F, 2F);
        GlStateManager.popMatrix();
    }

    private void renderEnchantmentText(ItemStack stack, int x, int y) {
        int enchantmentY = y - 8;
        int yCount = y;

        if (stack.getItem() == Items.GOLDEN_APPLE && stack.hasEffect()) {
            mc.fontRenderer.drawStringWithShadow("god", x * 2, enchantmentY, 0xFFc34d41);
            enchantmentY -= 8;
        }

        NBTTagList enchants = stack.getEnchantmentTagList();
        if(enchants.tagCount() > 2 && max.getValue()) {
            if(maxText.getValue()) {
                mc.fontRenderer.drawStringWithShadow("",(float)(x * 2), (float)enchantmentY, 0xFFc34d41);
                enchantmentY -= 8;
            } else {
                mc.fontRenderer.drawStringWithShadow("max", (float) (x * 2), (float) enchantmentY, 0xFFc34d41);
                enchantmentY -= 8;
            }
        } else {
            for (int index = 0; index < enchants.tagCount(); ++index) {
                short id = enchants.getCompoundTagAt(index).getShort("id");
                short level = enchants.getCompoundTagAt(index).getShort("lvl");
                Enchantment enc = Enchantment.getEnchantmentByID(id);
                if (enc != null) {
                    String encName = enc.isCurse()
                            ? TextFormatting.RED
                            + enc.getTranslatedName(level).substring(11).substring(0, 1).toLowerCase()
                            : enc.getTranslatedName(level).substring(0, 1).toLowerCase();
                    encName = encName + level;
                    mc.fontRenderer.drawStringWithShadow(encName, x * 2, enchantmentY, -1);
                    enchantmentY -= 8;
                }
            }
        }

        if(DamageUtil.hasDurability(stack)) {
            int percent = DamageUtil.getRoundedDamage(stack);
            String color;
            if(percent >= 60) {
                color = TextUtil.GREEN;
            } else if(percent >= 25) {
                color = TextUtil.YELLOW;
            } else {
                color = TextUtil.RED;
            }
            mc.fontRenderer.drawStringWithShadow(color + percent + "%", x * 2, enchantmentY, 0xFFFFFFFF);
        }
    }

    private float getBiggestArmorTag(EntityPlayer player) {
        float enchantmentY = 0;
        boolean arm = false;
        for (ItemStack stack : player.inventory.armorInventory) {
            float encY = 0;
            if (stack != null) {
                NBTTagList enchants = stack.getEnchantmentTagList();
                for (int index = 0; index < enchants.tagCount(); ++index) {
                    short id = enchants.getCompoundTagAt(index).getShort("id");
                    Enchantment enc = Enchantment.getEnchantmentByID(id);
                    if (enc != null) {
                        encY += 8;
                        arm = true;
                    }
                }
            }
            if (encY > enchantmentY) enchantmentY = encY;
        }
        ItemStack renderMainHand = player.getHeldItemMainhand().copy();
        if(renderMainHand.hasEffect()) {
            float encY = 0;
            NBTTagList enchants = renderMainHand.getEnchantmentTagList();
            for (int index = 0; index < enchants.tagCount(); ++index) {
                short id = enchants.getCompoundTagAt(index).getShort("id");
                Enchantment enc = Enchantment.getEnchantmentByID(id);
                if (enc != null) {
                    encY += 8;
                    arm = true;
                }
            }
            if (encY > enchantmentY) enchantmentY = encY;
        }
        ItemStack renderOffHand = player.getHeldItemOffhand().copy();
        if(renderOffHand.hasEffect()) {
            float encY = 0;
            NBTTagList enchants = renderOffHand.getEnchantmentTagList();
            for (int index = 0; index < enchants.tagCount(); ++index) {
                short id = enchants.getCompoundTagAt(index).getShort("id");
                Enchantment enc = Enchantment.getEnchantmentByID(id);
                if (enc != null) {
                    encY += 8;
                    arm = true;
                }
            }
            if (encY > enchantmentY) enchantmentY = encY;
        }
        return (arm ? 0 : 20) + enchantmentY;
    }

    private String getDisplayTag(EntityPlayer player) {
        String name = player.getDisplayName().getFormattedText();
        if(name.contains(mc.getSession().getUsername())) {
            name = "You";
        }

        if (!health.getValue()) {
            return name;
        }

        float health = player.getHealth();
        String color;

        if (health > 18) {
            color = TextUtil.GREEN;
        } else if (health > 16) {
            color = TextUtil.DARK_GREEN;
        } else if (health > 12) {
            color = TextUtil.YELLOW;
        } else if (health > 8) {
            color = TextUtil.RED;
        } else if (health > 5) {
            color = TextUtil.DARK_RED;
        } else {
            color = TextUtil.DARK_RED;
        }

        String pingStr = "";
        if(ping.getValue()) {
            try {
                final int responseTime = Objects.requireNonNull(mc.getConnection()).getPlayerInfo(player.getUniqueID()).getResponseTime();
                pingStr += responseTime + "ms ";
            } catch (Exception ignored) {}
        }

        String idString = "";
        if(entityID.getValue()) {
            idString += "ID: " + player.getEntityId() + " ";
        }

        String gameModeStr = "";
        if(gamemode.getValue()) {
            if(player.isCreative()) {
                gameModeStr += "[C] ";
            } else if(player.isSpectator() || player.isInvisible()) {
                gameModeStr += "[I] ";
            } else {
                gameModeStr += "[S] ";
            }
        }

        if(Math.floor(health) == health) {
            name = name + color + " " + (health > 0 ? (int) Math.floor(health) : "dead");
        } else {
            name = name + color + " " + (health > 0 ? (int) health : "dead");
        }
        return " " + pingStr + idString + gameModeStr + name + " ";
    }

    private double interpolate(double previous, double current, float delta) {
        return (previous + (current - previous) * delta);
    }

    // dont delete my fucking method again you mutant
    public static void drawBorderedRectReliant(final float x, final float y, final float x1, final float y1, final float lineWidth, final int inside, final int border) {
        enableGL2D();
        drawRect(x, y, x1, y1, inside);
        glColor(border);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glLineWidth(lineWidth);
        GL11.glBegin(3);
        GL11.glVertex2f(x, y);
        GL11.glVertex2f(x, y1);
        GL11.glVertex2f(x1, y1);
        GL11.glVertex2f(x1, y);
        GL11.glVertex2f(x, y);
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        disableGL2D();
    }

    // dont delete my fucking method again you mutant
    public static void drawRect(final Rectangle rectangle, final int color) {
        drawRect((float)rectangle.x, (float)rectangle.y, (float)(rectangle.x + rectangle.width), (float)(rectangle.y + rectangle.height), color);
    }

    // dont delete my fucking method again you mutant
    public static void drawRect(final float x, final float y, final float x1, final float y1, final int color) {
        enableGL2D();
        glColor(color);
        drawRect(x, y, x1, y1);
        disableGL2D();
    }

    // dont delete my fucking method again you mutant
    public static void drawRect(final float x, final float y, final float x1, final float y1, final float r, final float g, final float b, final float a) {
        enableGL2D();
        GL11.glColor4f(r, g, b, a);
        drawRect(x, y, x1, y1);
        disableGL2D();
    }

    // dont delete my fucking method again you mutant
    public static void drawRect(final float x, final float y, final float x1, final float y1) {
        GL11.glBegin(7);
        GL11.glVertex2f(x, y1);
        GL11.glVertex2f(x1, y1);
        GL11.glVertex2f(x1, y);
        GL11.glVertex2f(x, y);
        GL11.glEnd();
    }

    // dont delete my fucking method again you mutant
    public static void enableGL2D() {
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glDepthMask(true);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
    }

    // dont delete my fucking method again you mutant
    public static void disableGL2D() {
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
        GL11.glDisable(2848);
        GL11.glHint(3154, 4352);
        GL11.glHint(3155, 4352);
    }

    // dont delete my fucking method again you mutant
    public static void glColor(final Color color) {
        GL11.glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
    }

    // dont delete my fucking method again you mutant
    public static void glColor(final int hex) {
        final float alpha = (hex >> 24 & 0xFF) / 255.0f;
        final float red = (hex >> 16 & 0xFF) / 255.0f;
        final float green = (hex >> 8 & 0xFF) / 255.0f;
        final float blue = (hex & 0xFF) / 255.0f;
        GL11.glColor4f(red, green, blue, alpha);
    }

    // dont delete my fucking method again you mutant
    public static void glColor(final float alpha, final int redRGB, final int greenRGB, final int blueRGB) {
        final float red = 0.003921569f * redRGB;
        final float green = 0.003921569f * greenRGB;
        final float blue = 0.003921569f * blueRGB;
        GL11.glColor4f(red, green, blue, alpha);
    }

    @Override
    public void onTick() {
        if (ORainbow.getValue()) {
            OutlineRainbow();
        }
        if (NCRainbow.getValue()) {
            TextRainbow();
        }
        if (FCRainbow.getValue()) {
            FriendRainbow();
        }
        if (SCRainbow.getValue()) {
            SneakColorRainbow();
        }
        if (ICRainbow.getValue()) {
            InvisibleRainbow();
        }
        if (FORainbow.getValue()) {
            FriendOutlineRainbow();
        }
        if (IORainbow.getValue()) {
            InvisibleOutlineRainbow();
        }
        if (SORainbow.getValue()) {
            SneakOutlineRainbow();
        }

    }

    public void OutlineRainbow() {

        float[] tick_color = {
                (System.currentTimeMillis() % (360 * 32)) / (360f * 32)
        };

        int color_rgb_o = Color.HSBtoRGB(tick_color[0], 0.8f, 0.8f);

        Ored.setValue((color_rgb_o >> 16) & 0xFF);
        Ogreen.setValue((color_rgb_o >> 8) & 0xFF);
        Oblue.setValue(color_rgb_o & 0xFF);
    }

    public void TextRainbow() {
        float[] tick_color = {
                (System.currentTimeMillis() % (360 * 32)) / (360f * 32)
        };

        int color_rgb_o = Color.HSBtoRGB(tick_color[0], 0.8f, 0.8f);

        NCred.setValue((color_rgb_o >> 16) & 0xFF);
        NCgreen.setValue((color_rgb_o >> 8) & 0xFF);
        NCblue.setValue(color_rgb_o & 0xFF);
    }

    public void FriendRainbow() {
        float[] tick_color = {
                (System.currentTimeMillis() % (360 * 32)) / (360f * 32)
        };

        int color_rgb_o = Color.HSBtoRGB(tick_color[0], 0.8f, 0.8f);

        FCred.setValue((color_rgb_o >> 16) & 0xFF);
        FCgreen.setValue((color_rgb_o >> 8) & 0xFF);
        FCblue.setValue(color_rgb_o & 0xFF);
    }

    public void SneakColorRainbow() {
        float[] tick_color = {
                (System.currentTimeMillis() % (360 * 32)) / (360f * 32)
        };

        int color_rgb_o = Color.HSBtoRGB(tick_color[0], 0.8f, 0.8f);

        SCred.setValue((color_rgb_o >> 16) & 0xFF);
        SCgreen.setValue((color_rgb_o >> 8) & 0xFF);
        SCblue.setValue(color_rgb_o & 0xFF);
    }

    public void InvisibleOutlineRainbow() {
        float[] tick_color = {
                (System.currentTimeMillis() % (360 * 32)) / (360f * 32)
        };

        int color_rgb_o = Color.HSBtoRGB(tick_color[0], 0.8f, 0.8f);

        IOred.setValue((color_rgb_o >> 16) & 0xFF);
        IOgreen.setValue((color_rgb_o >> 8) & 0xFF);
        IOblue.setValue(color_rgb_o & 0xFF);
    }

    public void FriendOutlineRainbow() {
        float[] tick_color = {
                (System.currentTimeMillis() % (360 * 32)) / (360f * 32)
        };

        int color_rgb_o = Color.HSBtoRGB(tick_color[0], 0.8f, 0.8f);

        FOred.setValue((color_rgb_o >> 16) & 0xFF);
        FOgreen.setValue((color_rgb_o >> 8) & 0xFF);
        FOblue.setValue(color_rgb_o & 0xFF);
    }

    public void SneakOutlineRainbow() {
        float[] tick_color = {
                (System.currentTimeMillis() % (360 * 32)) / (360f * 32)
        };

        int color_rgb_o = Color.HSBtoRGB(tick_color[0], 0.8f, 0.8f);

        SOred.setValue((color_rgb_o >> 16) & 0xFF);
        SOgreen.setValue((color_rgb_o >> 8) & 0xFF);
        SOblue.setValue(color_rgb_o & 0xFF);
    }

    public void InvisibleRainbow() {
        float[] tick_color = {
                (System.currentTimeMillis() % (360 * 32)) / (360f * 32)
        };

        int color_rgb_o = Color.HSBtoRGB(tick_color[0], 0.8f, 0.8f);

        ICred.setValue((color_rgb_o >> 16) & 0xFF);
        ICgreen.setValue((color_rgb_o >> 8) & 0xFF);
        ICblue.setValue(color_rgb_o & 0xFF);
    }
}