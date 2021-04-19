package com.obamabob.runite.module.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.obamabob.runite.Runite;
import com.obamabob.runite.event.EventTarget;
import com.obamabob.runite.event.events.EventPacket;
import com.obamabob.runite.event.events.EventRender;
import com.obamabob.runite.friend.Friends;
import com.obamabob.runite.module.Module;
import com.obamabob.runite.settings.Setting;
import com.obamabob.runite.util.RenderUtil;
import com.obamabob.runite.util.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.potion.Potion;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.Explosion;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class AutoCrystal extends Module {
    public AutoCrystal() {super("AutoCrystal", Category.COMBAT);}

    Setting<String> breakMode = register(new Setting<>("Target", this, "Smart", new String[]{"All", "Smart", "Own"}));
    Setting<String> handBreak = register(new Setting<>("Hand", this, "Main", new String[]{"Main", "Offhand", "Both"}));
    Setting<String> breakType = register(new Setting<>("Type", this, "Swing", new String[]{"Swing", "Packet"}));
    Setting<Boolean> breakCrystal = register(new Setting<>("Break", this, true));
    Setting<Boolean> placeCrystal = register(new Setting<>("Place", this, true));
    Setting<Integer> attackSpeed = register(new Setting<>("Attack Speed", this,  16, 0, 20));
    Setting<Double> breakRange = register(new Setting<>("Hit Range", this, 4.4, 0.0, 10.0));
    Setting<Double> placeRange = register(new Setting<>("Place Range", this, 4.4, 0.0, 6.0));
    Setting<Double> wallsRange = register(new Setting<>("Walls Range", this, 3.5, 0.0, 10.0));
    Setting<Double> enemyRange = register(new Setting<>("Enemy Range", this, 6.0, 0.0, 16.0));
    Setting<Boolean> antiWeakness = register(new Setting<>("Anti Weakness", this,true));
    Setting<Boolean> antiSuicide = register(new Setting<>("Anti Suicide", this, true));
    Setting<Integer> antiSuicideValue = register(new Setting<>("Min Health", this,  14, 1, 36));
    Setting<Boolean> autoSwitch = register(new Setting<>("Switch", this, true));
    Setting<Boolean> noGapSwitch = register(new Setting<>("No Gap Switch", this, false));
    Setting<Boolean> multiPlace = register(new Setting<>("Multi Place", this, false));
    Setting<Boolean> endCrystalMode = register(new Setting<>("1.13 Place", this, false));
    Setting<Boolean> cancelCrystal = register(new Setting<>("Cancel Crystal", this, false));
    Setting<Double> minDmg = register(new Setting<>("Min Damage", this, 5.0, 0.0, 36.0));
    Setting<Double> minBreakDmg = register(new Setting<>("Min Break Dmg", this, 5.0, 0.0,36.0));
    Setting<Double> maxSelfDmg = register(new Setting<>("Max Self Dmg", this, 10.0, 1.0, 36.0));
    Setting<Integer> facePlaceValue = register(new Setting<>("FacePlace HP", this, 8, 0, 36));
    Setting<Boolean> rotate = register(new Setting<>("Rotate", this, true));
    Setting<Boolean> spoofRotations = register(new Setting<>("Spoof Angles", this, true));
    Setting<Boolean> raytrace = register(new Setting<>("Raytrace", this, false));
    Setting<Boolean> showDamage = register(new Setting<>("Render Dmg", this, true));
    Setting<String> hudDisplay = register(new Setting<>("HUD", this, "Mode", new String[]{"Mode", "Target", "None"}));
    Setting<Integer> r = register(new Setting<>("Red", this, 255, 0, 255));
    Setting<Integer> g = register(new Setting<>("Green", this, 255, 0, 255));
    Setting<Integer> b = register(new Setting<>("Blue", this, 255, 0, 255));
    Setting<Boolean> rainbow = register(new Setting<>("Rainbow", this, true));
    Setting<Boolean> fake = register(new Setting<>("Fake", this, false));

    private boolean switchCooldown = false;
    private boolean isAttacking = false;
    public boolean isActive = false;
    public static boolean stopAC = false;
    private static boolean togglePitch = false;
    private int oldSlot = -1;
    private int newSlot;
    private Entity renderEnt;
    private BlockPos render;
    private final ArrayList<BlockPos> PlacedCrystals = new ArrayList<BlockPos>();
    private EnumFacing enumFacing;
    Timer timer = new Timer();
    String enemy = "";

    public void onTick() {
        if (fake.getValue()) return;
        if (mc.player == null || mc.world == null || mc.player.isDead) {
            disable();
            return;
        }

        if (stopAC) {
            return;
        }

        if (antiSuicide.getValue() && (mc.player.getHealth() + mc.player.getAbsorptionAmount()) <= antiSuicideValue.getValue()) {
            return;
        }

        isActive = false;

        EntityEnderCrystal crystal = mc.world.loadedEntityList.stream()
                .filter(entity -> entity instanceof EntityEnderCrystal)
                .filter(e -> mc.player.getDistance(e) <= breakRange.getValue())
                .filter(e -> crystalCheck(e))
                .map(entity -> (EntityEnderCrystal) entity)
                .min(Comparator.comparing(c -> mc.player.getDistance(c)))
                .orElse(null);

        if (breakCrystal.getValue() && crystal != null) {

            if (!mc.player.canEntityBeSeen(crystal) && mc.player.getDistance(crystal) > wallsRange.getValue()) {
                return;
            }

            if (antiWeakness.getValue() && mc.player.isPotionActive(MobEffects.WEAKNESS)) {
                if (!isAttacking) {
                    // save initial player hand
                    oldSlot = mc.player.inventory.currentItem;
                    isAttacking = true;
                }
                // search for sword and tools in hotbar
                newSlot = -1;
                for (int i = 0; i < 9; i++) {
                    ItemStack stack = mc.player.inventory.getStackInSlot(i);
                    if (stack == ItemStack.EMPTY) {
                        continue;
                    }
                    if ((stack.getItem() instanceof ItemSword)) {
                        newSlot = i;
                        break;
                    }
                    if ((stack.getItem() instanceof ItemTool)) {
                        newSlot = i;
                        break;
                    }
                }
                // check if any swords or tools were found
                if (newSlot != -1) {
                    mc.player.inventory.currentItem = newSlot;
                    switchCooldown = true;
                }
            }

            if (timer.getTimePassed() / 50L >= 20 - attackSpeed.getValue()) {
                timer.reset();

                isActive = true;

                if (rotate.getValue()) {
                    lookAtPacket(crystal.posX, crystal.posY, crystal.posZ, mc.player);
                }

                if (breakType.getValue().equalsIgnoreCase("Swing")) {
                    breakCrystal(crystal);
                }
                else if (breakType.getValue().equalsIgnoreCase("Packet")) {
                    mc.player.connection.sendPacket(new CPacketUseEntity(crystal));
                    swingArm();
                }

                if (cancelCrystal.getValue()) {
                    crystal.setDead();
                    mc.world.removeAllEntities();
                    mc.world.getLoadedEntityList();
                }

                isActive = false;
            }

            if (!multiPlace.getValue()) {
                return;
            }
        }
        else {
            resetRotation();
            if (oldSlot != -1) {
                mc.player.inventory.currentItem = oldSlot;
                oldSlot = -1;
            }
            isAttacking = false;
            isActive = false;
        }

        int crystalSlot = mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL ? mc.player.inventory.currentItem : -1;
        if (crystalSlot == -1) {
            for (int l = 0; l < 9; ++l) {
                if (mc.player.inventory.getStackInSlot(l).getItem() == Items.END_CRYSTAL) {
                    if (mc.player.getHeldItem(EnumHand.OFF_HAND).getItem() != Items.END_CRYSTAL) {
                        crystalSlot = l;
                        break;
                    }
                }
            }
        }
        boolean offhand = false;
        if (mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) {
            offhand = true;
        }
        else if (crystalSlot == -1) {
            return;
        }

        List<BlockPos> blocks = findCrystalBlocks();

        List<Entity> entities = new ArrayList<>();
        entities.addAll(mc.world.playerEntities.stream().filter(entityPlayer -> !Friends.isFriend(entityPlayer.getName())).sorted(Comparator.comparing(e -> mc.player.getDistance(e))).collect(Collectors.toList()));

        BlockPos q = null;
        double damage = 0.5D;
        Iterator var9 = entities.iterator();

        label164:
        while (true) {
            EntityPlayer entity = null;
            do { do {
                if (!var9.hasNext()) {
                    if (damage == 0.5D) {
                        this.render = null;
                        this.renderEnt = null;
                        resetRotation();
                        return;
                    }

                    this.render = q;
                    if (this.placeCrystal.getValue()) {
                        if (!offhand && mc.player.inventory.currentItem != crystalSlot) {
                            if (this.autoSwitch.getValue()) {
                                if (!noGapSwitch.getValue() || !(mc.player.getHeldItemMainhand().getItem() == Items.GOLDEN_APPLE)) {
                                    mc.player.inventory.currentItem = crystalSlot;
                                    resetRotation();
                                    this.switchCooldown = true;
                                }
                            }
                            return;
                        }

                        if (rotate.getValue()) {
                            this.lookAtPacket((double) q.getX() + 0.5D, (double) q.getY() - 0.5D, (double) q.getZ() + 0.5D, mc.player);
                        }

                        RayTraceResult result = mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + (double) mc.player.getEyeHeight(), mc.player.posZ), new Vec3d((double) q.getX() + 0.5D, (double) q.getY() - 0.5D, (double) q.getZ() + 0.5D));
                        if (raytrace.getValue()) {
                            if (result == null || result.sideHit == null) {
                                q = null;
                                enumFacing = null;
                                render = null;
                                resetRotation();
                                isActive = false;
                                return;
                            }
                            else {
                                enumFacing = result.sideHit;
                            }
                        }

                        if (this.switchCooldown) {
                            this.switchCooldown = false;
                            return;
                        }

                        if (q != null && mc.player != null) {
                            isActive = true;
                            if (raytrace.getValue() && enumFacing != null) {
                                mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(q, enumFacing, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0, 0, 0));
                            }
                            else if (q.getY() == 255) {
                                // For Hoosiers. This is how we do buildheight. If the target block (q) is at Y 255. Then we send a placement packet to the bottom part of the block. Thus the EnumFacing.DOWN.
                                mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(q, EnumFacing.DOWN, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0, 0, 0));
                            }
                            else {
                                mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(q, EnumFacing.UP, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0, 0, 0));
                            }
                            mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
                            //Cache the crystals we've placed
                            PlacedCrystals.add(q);
                        }

                        if (isSpoofingAngles) {
                            EntityPlayerSP var10000;
                            if (togglePitch) {
                                var10000 = mc.player;
                                var10000.rotationPitch = (float) ((double) var10000.rotationPitch + 4.0E-4D);
                                togglePitch = false;
                            }
                            else {
                                var10000 = mc.player;
                                var10000.rotationPitch = (float) ((double) var10000.rotationPitch - 4.0E-4D);
                                togglePitch = true;
                            }
                        }

                        return;
                    }
                }

                entity = (EntityPlayer) var9.next();
            } while (entity == mc.player);
            } while (entity.getHealth() <= 0.0F);

            Iterator var11 = blocks.iterator();

            while (true) {
                BlockPos blockPos;
                double d;
                double self;
                double targetDamage;
                float targetHealth;
                double x;
                double y;
                double z;
                do { do { do { do { double b; do {
                    if (!var11.hasNext()) {
                        continue label164;
                    }

                    enemy = entity.getName();

                    blockPos = (BlockPos) var11.next();
                    b = entity.getDistanceSq(blockPos);
                    // Better method for doing EnemyRange
                    // @author Cyber
                    x = blockPos.getX() + 0.0;
                    y = blockPos.getY() + 1.0;
                    z = blockPos.getZ() + 0.0;
                    // } while (b >= 169.0D);
                } while (entity.getDistanceSq(x, y , z) >= enemyRange.getValue() * enemyRange.getValue());

                    d = calculateDamage((double) blockPos.getX() + 0.5D, blockPos.getY() + 1, (double) blockPos.getZ() + 0.5D, entity);
                } while (d <= damage);
                    targetDamage = calculateDamage(blockPos.getX() + 0.5, blockPos.getY() + 1, blockPos.getZ() + 0.5, entity);
                    targetHealth = entity.getHealth() + entity.getAbsorptionAmount();
                } while (targetDamage < minDmg.getValue() && targetHealth > facePlaceValue.getValue());
                    self = calculateDamage((double) blockPos.getX() + 0.5D, blockPos.getY() + 1, (double) blockPos.getZ() + 0.5D, mc.player);
                } while (self >= maxSelfDmg.getValue());
                } while(self >= mc.player.getHealth() + mc.player.getAbsorptionAmount());

                damage = d;
                q = blockPos;
                renderEnt = entity;
            }
        }
    }

    public void onWorldRender(EventRender event) {
        if (this.render != null) {
            Color temp = Color.decode(String.valueOf(Runite.rgb));
            RenderUtil.drawBox(this.render,1, (rainbow.getValue() ? new Color(temp.getRed(), temp.getGreen(), temp.getBlue(), 50) : new Color(r.getValue(), g.getValue(), b.getValue(), 50)), 63);
            RenderUtil.drawBoundingBox(this.render, 1, 1.00f, (rainbow.getValue() ? new Color(temp.getRed(), temp.getGreen(), temp.getBlue(), 255) : new Color(r.getValue(), g.getValue(), b.getValue(), 255)));
        }

        if(showDamage.getValue()) {
            if (this.render != null && this.renderEnt != null) {
                double d = calculateDamage(render.getX() + .5, render.getY() + 1, render.getZ() + .5, renderEnt);
                String[] damageText=new String[1];
                damageText[0]=(Math.floor(d) == d ? (int) d : String.format("%.1f", d)) + "";
                RenderUtil.drawNametag(render.getX()+0.5,render.getY()+0.5,render.getZ()+0.5,damageText,new Color(255,255,255),1);
            }
        }
    }

    private void lookAtPacket(double px, double py, double pz, EntityPlayer me) {
        double[] v = calculateLookAt(px, py, pz, me);
        setYawAndPitch((float) v[0], (float) v[1]);
    }

    private boolean crystalCheck(Entity crystal) {

        if (!(crystal instanceof EntityEnderCrystal)) {
            return false;
        }

        if (breakMode.getValue().equalsIgnoreCase("All")) {
            return true;
        }
        else if (breakMode.getValue().equalsIgnoreCase("Own")) {
            for (BlockPos pos : new ArrayList<>(PlacedCrystals)) {
                if (pos != null && pos.getDistance((int)crystal.posX, (int)crystal.posY, (int)crystal.posZ) <= 3.0) {
                    return true;
                }
            }
        }
        else if (breakMode.getValue().equalsIgnoreCase("Smart")) {
            EntityLivingBase target = renderEnt != null ? (EntityLivingBase) renderEnt : GetNearTarget(crystal);

            if (target == null || target == mc.player) {
                return false;
            }

            float targetDmg = calculateDamage(crystal.posX + 0.5, crystal.posY + 1, crystal.posZ + 0.5, target);

            return targetDmg >= minBreakDmg.getValue() || (targetDmg > minBreakDmg.getValue()) && target.getHealth() > facePlaceValue.getValue();
        }

        return false;
    }

    private boolean validTarget(Entity entity) {
        if (entity == null)
            return false;

        if (!(entity instanceof EntityLivingBase))
            return false;

        if (Friends.isFriend(entity.getName()))
            return false;

        if (entity.isDead || ((EntityLivingBase) entity).getHealth() <= 0.0F)
            return false;

        if (entity instanceof EntityPlayer) {
            return entity != mc.player;
        }

        return false;
    }

    private EntityLivingBase GetNearTarget(Entity distanceTarget) {
        return mc.world.loadedEntityList.stream()
                .filter(entity -> validTarget(entity))
                .map(entity -> (EntityLivingBase) entity)
                .min(Comparator.comparing(entity -> distanceTarget.getDistance(entity)))
                .orElse(null);
    }

    public boolean canPlaceCrystal(BlockPos blockPos) {
        BlockPos boost = blockPos.add(0, 1, 0);
        BlockPos boost2 = blockPos.add(0, 2, 0);
        if (!endCrystalMode.getValue())
            return (mc.world.getBlockState(blockPos).getBlock() == Blocks.BEDROCK
                    || mc.world.getBlockState(blockPos).getBlock() == Blocks.OBSIDIAN)
                    && mc.world.getBlockState(boost).getBlock() == Blocks.AIR
                    && mc.world.getBlockState(boost2).getBlock() == Blocks.AIR
                    && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost)).isEmpty()
                    && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2)).isEmpty();
        else
            return (mc.world.getBlockState(blockPos).getBlock() == Blocks.BEDROCK
                    || mc.world.getBlockState(blockPos).getBlock() == Blocks.OBSIDIAN)
                    && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost)).isEmpty()
                    && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2)).isEmpty();
    }

    public static BlockPos getPlayerPos() {
        return new BlockPos(Math.floor(Minecraft.getMinecraft().player.posX), Math.floor(Minecraft.getMinecraft().player.posY), Math.floor(Minecraft.getMinecraft().player.posZ));
    }

    private List<BlockPos> findCrystalBlocks() {
        NonNullList<BlockPos> positions = NonNullList.create();
        positions.addAll(getSphere(getPlayerPos(), placeRange.getValue().floatValue(), placeRange.getValue().intValue(), false, true, 0).stream().filter(this::canPlaceCrystal).collect(Collectors.toList()));
        return positions;
    }

    public List<BlockPos> getSphere(BlockPos loc, float r, int h, boolean hollow, boolean sphere, int plus_y) {
        List<BlockPos> circleblocks = new ArrayList<>();
        int cx = loc.getX();
        int cy = loc.getY();
        int cz = loc.getZ();
        for (int x = cx - (int) r; x <= cx + r; x++) {
            for (int z = cz - (int) r; z <= cz + r; z++) {
                for (int y = (sphere ? cy - (int) r : cy); y < (sphere ? cy + r : cy + h); y++) {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if (dist < r * r && !(hollow && dist < (r - 1) * (r - 1))) {
                        BlockPos l = new BlockPos(x, y + plus_y, z);
                        circleblocks.add(l);
                    }
                }
            }
        }
        return circleblocks;
    }

    public static float calculateDamage(double posX, double posY, double posZ, Entity entity) {
        float doubleExplosionSize = 12.0F;
        double distancedsize = entity.getDistance(posX, posY, posZ) / (double) doubleExplosionSize;
        Vec3d vec3d = new Vec3d(posX, posY, posZ);
        double blockDensity = entity.world.getBlockDensity(vec3d, entity.getEntityBoundingBox());
        double v = (1.0D - distancedsize) * blockDensity;
        float damage = (float) ((int) ((v * v + v) / 2.0D * 7.0D * (double) doubleExplosionSize + 1.0D));
        double finald = 1.0D;

        if (entity instanceof EntityLivingBase) {
            finald = getBlastReduction((EntityLivingBase) entity, getDamageMultiplied(damage), new Explosion(Minecraft.getMinecraft().world, null, posX, posY, posZ, 6F, false, true));
        }
        return (float) finald;
    }

    public static float getBlastReduction(EntityLivingBase entity, float damage, Explosion explosion) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer ep = (EntityPlayer) entity;
            DamageSource ds = DamageSource.causeExplosionDamage(explosion);
            damage = CombatRules.getDamageAfterAbsorb(damage, (float) ep.getTotalArmorValue(), (float) ep.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());

            int k = EnchantmentHelper.getEnchantmentModifierDamage(ep.getArmorInventoryList(), ds);
            float f = MathHelper.clamp(k, 0.0F, 20.0F);
            damage *= 1.0F - f / 25.0F;

            if (entity.isPotionActive(Potion.getPotionById(11))) {
                damage = damage - (damage / 4);
            }
            damage = Math.max(damage, 0.0F);
            return damage;
        }
        damage = CombatRules.getDamageAfterAbsorb(damage, (float) entity.getTotalArmorValue(), (float) entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
        return damage;
    }

    private static boolean isSpoofingAngles;
    private static double yaw;
    private static double pitch;

    private static float getDamageMultiplied(float damage) {
        int diff = Minecraft.getMinecraft().world.getDifficulty().getId();
        return damage * (diff == 0 ? 0 : (diff == 2 ? 1 : (diff == 1 ? 0.5f : 1.5f)));
    }

    private static void setYawAndPitch(float yaw1, float pitch1) {
        yaw = yaw1;
        pitch = pitch1;
        isSpoofingAngles = true;
    }

    private static void resetRotation() {
        if (isSpoofingAngles) {
            yaw = Minecraft.getMinecraft().player.rotationYaw;
            pitch = Minecraft.getMinecraft().player.rotationPitch;
            isSpoofingAngles = false;
        }
    }

    public static double[] calculateLookAt(double px, double py, double pz, EntityPlayer me) {
        double dirx = me.posX - px;
        double diry = me.posY - py;
        double dirz = me.posZ - pz;

        double len = Math.sqrt(dirx*dirx + diry*diry + dirz*dirz);

        dirx /= len;
        diry /= len;
        dirz /= len;

        double pitch = Math.asin(diry);
        double yaw = Math.atan2(dirz, dirx);

        pitch = pitch * 180.0d / Math.PI;
        yaw = yaw * 180.0d / Math.PI;

        yaw += 90f;

        return new double[]{yaw,pitch};
    }

    private void breakCrystal(EntityEnderCrystal crystal) {
        mc.playerController.attackEntity(mc.player, crystal);

        swingArm();
    }

    private void swingArm() {
        if (handBreak.getValue().equalsIgnoreCase("Both") && mc.player.getHeldItemOffhand() != null) {
            mc.player.swingArm(EnumHand.MAIN_HAND);
            mc.player.swingArm(EnumHand.OFF_HAND);
        }
        else if (handBreak.getValue().equalsIgnoreCase("Offhand") && mc.player.getHeldItemOffhand() != null) {
            mc.player.swingArm(EnumHand.OFF_HAND);
        }
        else {
            mc.player.swingArm(EnumHand.MAIN_HAND);
        }
    }

    @EventTarget
    public void packetSendListener(EventPacket.Send event) {
        Packet packet = event.getPacket();
        if (packet instanceof CPacketPlayer && spoofRotations.getValue()) {
            if (isSpoofingAngles) {
                ((CPacketPlayer) packet).yaw = (float) yaw;
                ((CPacketPlayer) packet).pitch = (float) pitch;
            }
        }
    }

    @EventTarget
    public void packetReceiveListener(EventPacket.Receive event) {
        if (event.getPacket() instanceof SPacketSoundEffect) {
            final SPacketSoundEffect packet = (SPacketSoundEffect) event.getPacket();
            if (packet.getCategory() == SoundCategory.BLOCKS && packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
                for (Entity e : Minecraft.getMinecraft().world.loadedEntityList) {
                    if (e instanceof EntityEnderCrystal) {
                        if (e.getDistance(packet.getX(), packet.getY(), packet.getZ()) <= 6.0f) {
                            e.setDead();
                        }
                    }
                }
            }
        }
    }

    public void onEnable() {
        PlacedCrystals.clear();
        isActive = false;
    }

    public void onDisable() {
        render = null;
        renderEnt = null;
        resetRotation();
        PlacedCrystals.clear();
        isActive = false;
    }

    public String getHudInfo() {
        String t = "";
        if (hudDisplay.getValue().equalsIgnoreCase("Mode")) return "[" + ChatFormatting.WHITE + breakMode.getValue() + ChatFormatting.RESET + "]";
        if (hudDisplay.getValue().equalsIgnoreCase("None")) t = "";
        if (hudDisplay.getValue().equalsIgnoreCase("Target")) t = "[" + ChatFormatting.WHITE + enemy + ChatFormatting.RESET + "]";
        return t;
    }
}
