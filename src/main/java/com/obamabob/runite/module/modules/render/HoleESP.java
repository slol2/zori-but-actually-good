package com.obamabob.runite.module.modules.render;

import java.awt.Color;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.obamabob.runite.event.events.EventRender;
import com.obamabob.runite.settings.Setting;
import com.obamabob.runite.util.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.obamabob.runite.module.Module;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.init.Blocks;

import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.input.Keyboard;

public class HoleESP extends Module {
    private final BlockPos[] surroundOffset;
    private final Setting<Boolean> frustum = register(new Setting<>("Frustum", this, true));
    private final Setting<Boolean> hideOwn = register(new Setting<>("HideOwn", this, false));
    private final Setting<Boolean> offset = register(new Setting<>("Offset Lower", this, false));
    private final Setting<Boolean> future = register(new Setting<>("Future Mode", this, true));
    private final Setting<Float> renderDistance = register(new Setting<>("RenderDistance", this, 8f, 1f, 32f));
    private final Setting<Boolean> max = register(new Setting<>("Maximum Holes", this, false));
    private final Setting<Integer> maxHoles = register(new Setting<>("Maximum Num", this, 8, 1, 100));
    private final Setting<String> holeMode = register(new Setting<>("Hole Mode", this, "Both", new String[]{"Bedrock", "Obsidian", "Both"}));
    private final Setting<String> renderMode = register(new Setting<>("RenderMode", this, "Solid", new ArrayList<>(
            Arrays.asList("Solid", "Flat")
    )));
    private final Setting<String> drawMode = register(new Setting<>("DrawMode", this, "Solid", EnumUtil.enumConverter(Modes.class)));
    private final Setting<Float> cuboid = register(new Setting<>("Cuboid Height", this, 0.9f, 0f, 1f));
    private final Setting<Boolean> rainbow = register(new Setting<>("Rainbow", this, false));
    private final Setting<Integer> obiRed = register(new Setting<>("ObiRed", this, 255, 0, 255));
    private final Setting<Integer> obiGreen = register(new Setting<>("ObiGreen", this, 0, 0, 255));
    private final Setting<Integer> obiBlue = register(new Setting<>("ObiBlue", this, 0, 0, 255));
    private final Setting<Integer> brockRed = register(new Setting<>("BrockRed", this, 0, 0, 255));
    private final Setting<Integer> brockGreen = register(new Setting<>("BrockGreen", this, 255, 0, 255));
    private final Setting<Integer> brockBlue = register(new Setting<>("BrockBlue", this, 0, 0, 255));
    private final Setting<Integer> alpha = register(new Setting<>("Alpha", this, 120, 0, 255));
    private final Setting<Integer> alpha2 = register(new Setting<>("Outline Alpha", this, 255, 0, 255));
    private ConcurrentHashMap<BlockPos, Pair<Boolean, Boolean>> safeHoles;

    public HoleESP() {
        super("HoleESP", Category.RENDER);
        this.surroundOffset = new BlockPos[] { new BlockPos(0, -1, 0), new BlockPos(0, 0, -1), new BlockPos(1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(-1, 0, 0) };
    }


    public enum Modes {
        SOLID, OUTLINE, FULL, CUBOID, INDICATOR
    }
    int holes;

    ICamera camera = new Frustum();

    @Override
    public void onTick() {
        if (mc.world == null) return;
        double d3 = mc.player.lastTickPosX + (mc.player.posX - mc.player.lastTickPosX) * (double)mc.getRenderPartialTicks();
        double d4 = mc.player.lastTickPosY + (mc.player.posY - mc.player.lastTickPosY) * (double)mc.getRenderPartialTicks();
        double d5 = mc.player.lastTickPosZ + (mc.player.posZ - mc.player.lastTickPosZ) * (double)mc.getRenderPartialTicks();

        camera.setPosition(d3,  d4,  d5);
        if (this.safeHoles == null) {
            this.safeHoles = new ConcurrentHashMap<>();
        }
        else {
            this.safeHoles.clear();
        }
        final int range = (int)Math.ceil(this.renderDistance.getValue());
        final List<BlockPos> blockPosList = BlockInteractionHelper.getSphere(new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ)), (float)range, range, false, true, 0);
        holes = 0;
        for (final BlockPos pos : blockPosList) {
            if (!mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR)) {
                continue;
            }
            if (!mc.world.getBlockState(pos.add(0, 1, 0)).getBlock().equals(Blocks.AIR)) {
                continue;
            }
            if (!mc.world.getBlockState(pos.add(0, 2, 0)).getBlock().equals(Blocks.AIR)) {
                continue;
            }
            if (this.hideOwn.getValue() && pos.equals(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ))) {
                continue;
            }
            if (frustum.getValue() && !camera.isBoundingBoxInFrustum(mc.world.getBlockState(pos).getSelectedBoundingBox(mc.world, pos))) {
                continue;
            }
            boolean isSafe = true;
            boolean isBedrock = true;
            boolean hasBedrock = false;
            for (final BlockPos offset : this.surroundOffset) {
                final Block block = mc.world.getBlockState(pos.add(offset)).getBlock();
                if (block != Blocks.BEDROCK) {
                    isBedrock = false;
                }
                if (block == Blocks.BEDROCK) {
                    hasBedrock = true;
                }
                if (block != Blocks.BEDROCK && block != Blocks.OBSIDIAN && block != Blocks.ENDER_CHEST && block != Blocks.ANVIL) {
                    isSafe = false;
                    break;
                }
            }
            if (!isSafe) {
                continue;
            }
            if (isBedrock && holeMode.getValue().equalsIgnoreCase("Obsidian")) continue;
            if (!isBedrock && holeMode.getValue().equalsIgnoreCase("Bedrock")) continue;
            this.safeHoles.put(pos, new Pair<>(isBedrock, hasBedrock));
            if (this.max.getValue()) {
                holes++;
                if (holes == this.maxHoles.getValue()) {
                    return;
                }
            }
        }
    }

    @Override
    public void onWorldRender(final EventRender event) {
        if (mc.player == null || this.safeHoles == null) {
            return;
        }
        if (this.safeHoles.isEmpty()) {
            return;
        }
        if (this.drawMode.getValue().equalsIgnoreCase("Solid")) {
            RuniteTessellator.prepare(7);
            this.safeHoles.forEach((blockPos, pair) -> {
                if (this.offset.getValue()) {
                    blockPos = blockPos.add(0, -1, 0);
                }
                if (pair.getKey()) {
                    this.drawBlock(blockPos, (this.rainbow.getValue() ? RainbowUtil.r : this.brockRed.getValue()), (this.rainbow.getValue() ? RainbowUtil.g : this.brockGreen.getValue()), (this.rainbow.getValue() ? RainbowUtil.b : this.brockBlue.getValue()), pair);
                } else {
                    this.drawBlock(blockPos, (this.rainbow.getValue() ? RainbowUtil.r : this.obiRed.getValue()), (this.rainbow.getValue() ? RainbowUtil.g : this.obiGreen.getValue()), (this.rainbow.getValue() ? RainbowUtil.b : this.obiBlue.getValue()), pair);
                }
                return;
            });
            RuniteTessellator.release();
        }
        else if (this.drawMode.getValue().equalsIgnoreCase("Outline")) {
            this.safeHoles.forEach((blockPos, pair) -> {
                if (this.offset.getValue()) {
                    blockPos = blockPos.add(0, -1, 0);
                }
                if (renderMode.getValue().equalsIgnoreCase("Solid")) {
                    if (pair.getKey()) {
                        this.drawBlockO(blockPos, (this.rainbow.getValue() ? RainbowUtil.r : this.brockRed.getValue()), (this.rainbow.getValue() ? RainbowUtil.g : this.brockGreen.getValue()), (this.rainbow.getValue() ? RainbowUtil.b : this.brockBlue.getValue()), pair);
                    } else {
                        this.drawBlockO(blockPos, (this.rainbow.getValue() ? RainbowUtil.r : this.obiRed.getValue()), (this.rainbow.getValue() ? RainbowUtil.g : this.obiGreen.getValue()), (this.rainbow.getValue() ? RainbowUtil.b : this.obiBlue.getValue()), pair);
                    }
                }
                else if (renderMode.getValue().equalsIgnoreCase("Flat")) {
                    if (pair.getKey()) {
                        this.drawBlockOF(blockPos, (this.rainbow.getValue() ? RainbowUtil.r : this.brockRed.getValue()), (this.rainbow.getValue() ? RainbowUtil.g : this.brockGreen.getValue()), (this.rainbow.getValue() ? RainbowUtil.b : this.brockBlue.getValue()), pair);
                    } else {
                        this.drawBlockOF(blockPos, (this.rainbow.getValue() ? RainbowUtil.r : this.obiRed.getValue()), (this.rainbow.getValue() ? RainbowUtil.g : this.obiGreen.getValue()), (this.rainbow.getValue() ? RainbowUtil.b : this.obiBlue.getValue()), pair);
                    }
                }
            });
        }
        else if (this.drawMode.getValue().equalsIgnoreCase("Full")) {
            this.safeHoles.forEach((blockPos, pair) -> {
                if (this.offset.getValue()) {
                    blockPos = blockPos.add(0, -1, 0);
                }
                if (renderMode.getValue().equalsIgnoreCase("Solid")) {
                    if (pair.getKey()) {
                        RuniteTessellator.prepare(7);
                        this.drawBlock(blockPos, (this.rainbow.getValue() ? RainbowUtil.r : this.brockRed.getValue()), (this.rainbow.getValue() ? RainbowUtil.g : this.brockGreen.getValue()), (this.rainbow.getValue() ? RainbowUtil.b : this.brockBlue.getValue()), pair);
                        RuniteTessellator.release();
                        this.drawBlockO(blockPos, (this.rainbow.getValue() ? RainbowUtil.r : this.brockRed.getValue()), (this.rainbow.getValue() ? RainbowUtil.g : this.brockGreen.getValue()), (this.rainbow.getValue() ? RainbowUtil.b : this.brockBlue.getValue()), pair);
                    } else {
                        RuniteTessellator.prepare(7);
                        this.drawBlock(blockPos, (this.rainbow.getValue() ? RainbowUtil.r : this.obiRed.getValue()), (this.rainbow.getValue() ? RainbowUtil.g : this.obiGreen.getValue()), (this.rainbow.getValue() ? RainbowUtil.b : this.obiBlue.getValue()), pair);
                        RuniteTessellator.release();
                        this.drawBlockO(blockPos, (this.rainbow.getValue() ? RainbowUtil.r : this.obiRed.getValue()), (this.rainbow.getValue() ? RainbowUtil.g : this.obiGreen.getValue()), (this.rainbow.getValue() ? RainbowUtil.b : this.obiBlue.getValue()), pair);
                    }
                }
                else if (renderMode.getValue().equalsIgnoreCase("Flat")) {
                    if (pair.getKey()) {
                        RuniteTessellator.prepare(7);
                        this.drawBlock(blockPos, (this.rainbow.getValue() ? RainbowUtil.r : this.brockRed.getValue()), (this.rainbow.getValue() ? RainbowUtil.g : this.brockGreen.getValue()), (this.rainbow.getValue() ? RainbowUtil.b : this.brockBlue.getValue()), pair);
                        RuniteTessellator.release();
                        this.drawBlockOF(blockPos, (this.rainbow.getValue() ? RainbowUtil.r : this.brockRed.getValue()), (this.rainbow.getValue() ? RainbowUtil.g : this.brockGreen.getValue()), (this.rainbow.getValue() ? RainbowUtil.b : this.brockBlue.getValue()), pair);
                    } else {
                        RuniteTessellator.prepare(7);
                        this.drawBlock(blockPos, (this.rainbow.getValue() ? RainbowUtil.r : this.obiRed.getValue()), (this.rainbow.getValue() ? RainbowUtil.g : this.obiGreen.getValue()), (this.rainbow.getValue() ? RainbowUtil.b : this.obiBlue.getValue()), pair);
                        RuniteTessellator.release();
                        this.drawBlockOF(blockPos, (this.rainbow.getValue() ? RainbowUtil.r : this.obiRed.getValue()), (this.rainbow.getValue() ? RainbowUtil.g : this.obiGreen.getValue()), (this.rainbow.getValue() ? RainbowUtil.b : this.obiBlue.getValue()), pair);
                    }
                }
            });
        }
        else if (this.drawMode.getValue().equalsIgnoreCase("Cuboid")) {
            this.safeHoles.forEach((blockPos, pair) -> {
                if (this.offset.getValue()) {
                    blockPos = blockPos.add(0, -1, 0);
                }
                if (pair.getKey()) {
                    this.drawBlockCUB(blockPos, (this.rainbow.getValue() ? RainbowUtil.r : this.brockRed.getValue()), (this.rainbow.getValue() ? RainbowUtil.g : this.brockGreen.getValue()), (this.rainbow.getValue() ? RainbowUtil.b : this.brockBlue.getValue()), pair);
                } else {
                    this.drawBlockCUB(blockPos, (this.rainbow.getValue() ? RainbowUtil.r : this.obiRed.getValue()), (this.rainbow.getValue() ? RainbowUtil.g : this.obiGreen.getValue()), (this.rainbow.getValue() ? RainbowUtil.b : this.obiBlue.getValue()), pair);
                }
            });
        }
        else if (this.drawMode.getValue().equalsIgnoreCase("Indicator")) {
            this.safeHoles.forEach((blockPos, pair) -> {
                if (this.offset.getValue()) {
                    blockPos = blockPos.add(0, -1, 0);
                }
                if (pair.getKey()) {
                    this.drawBlockIndicator(blockPos, (this.rainbow.getValue() ? RainbowUtil.r : this.brockRed.getValue()), (this.rainbow.getValue() ? RainbowUtil.g : this.brockGreen.getValue()), (this.rainbow.getValue() ? RainbowUtil.b : this.brockBlue.getValue()), pair);
                } else {
                    this.drawBlockIndicator(blockPos, (this.rainbow.getValue() ? RainbowUtil.r : this.obiRed.getValue()), (this.rainbow.getValue() ? RainbowUtil.g : this.obiGreen.getValue()), (this.rainbow.getValue() ? RainbowUtil.b : this.obiBlue.getValue()), pair);
                }
            });
        }
    }

    private boolean isIntermediate(final BlockPos pos) {
        boolean flag = false;
        boolean oflag = false;
        for (final BlockPos offset : this.surroundOffset) {
            final Block block = mc.world.getBlockState(pos.add(offset)).getBlock();
            if (block == Blocks.BEDROCK) {
                flag = true;
            }
            else if (block == Blocks.OBSIDIAN && block == Blocks.ENDER_CHEST && block == Blocks.ANVIL) {
                oflag = true;
            }
        }
        return flag && oflag;
    }

    private void drawBlock(final BlockPos blockPos, final int r, final int g, final int b, final Pair<Boolean, Boolean> pair) {
        Color color = new Color(r, g, b, this.alpha.getValue());
        if (future.getValue() && (!pair.getKey() && pair.getValue())) {
            color = new Color(255, 255, 0, this.alpha.getValue());
        }
        int mask = 1;
        if (this.renderMode.getValue().equalsIgnoreCase("Solid")) {
            mask = 63;
        }
        RuniteTessellator.drawBox(blockPos, color.getRGB(), mask);
    }
    private void drawBlockO(final BlockPos blockPos, final int r, final int g, final int b, final Pair<Boolean, Boolean> pair) {
        final int red = (future.getValue() && (!pair.getKey() && pair.getValue()) ? 255 : r);
        final int green = (future.getValue() && (!pair.getKey() && pair.getValue()) ? 255 : g);
        final int blue = (future.getValue() && (!pair.getKey() && pair.getValue()) ? 0 : b);
        final IBlockState iBlockState2 = mc.world.getBlockState(blockPos);
        final Vec3d interp2 = MathUtil.interpolateEntity(mc.player, mc.getRenderPartialTicks());
        RuniteTessellator.drawBoundingBox(iBlockState2.getSelectedBoundingBox(mc.world, blockPos).grow(0.0020000000949949026D).offset(-interp2.x, -interp2.y, -interp2.z), 1.5f, red, green, blue, alpha2.getValue());
    }
    private void drawBlockCUB(final BlockPos blockPos, final int r, final int g, final int b, final Pair<Boolean, Boolean> pair) {
        final int red = (future.getValue() && (!pair.getKey() && pair.getValue()) ? 255 : r);
        final int green = (future.getValue() && (!pair.getKey() && pair.getValue()) ? 255 : g);
        final int blue = (future.getValue() && (!pair.getKey() && pair.getValue()) ? 0 : b);
        final IBlockState iBlockState2 = mc.world.getBlockState(blockPos);
        final Vec3d interp2 = MathUtil.interpolateEntity(mc.player, mc.getRenderPartialTicks());
        AxisAlignedBB aabb = iBlockState2.getSelectedBoundingBox(mc.world, blockPos);
        aabb = aabb.setMaxY(aabb.maxY - 1 * cuboid.getValue()).grow(0.0020000000949949026D).offset(-interp2.x, -interp2.y, -interp2.z);
        RuniteTessellator.drawFullBox2(aabb, blockPos, 1.5f, new Color(red, green, blue, alpha.getValue()).getRGB(), alpha2.getValue());
    }

    private void drawBlockIndicator(final BlockPos blockPos, final int r, final int g, final int b, final Pair<Boolean, Boolean> pair) {
        final int red = (future.getValue() && (!pair.getKey() && pair.getValue()) ? 255 : r);
        final int green = (future.getValue() && (!pair.getKey() && pair.getValue()) ? 255 : g);
        final int blue = (future.getValue() && (!pair.getKey() && pair.getValue()) ? 0 : b);
        final IBlockState iBlockState2 = mc.world.getBlockState(blockPos);
        final Vec3d interp2 = MathUtil.interpolateEntity(mc.player, mc.getRenderPartialTicks());
        AxisAlignedBB aabb = iBlockState2.getSelectedBoundingBox(mc.world, blockPos);
        aabb = aabb.setMaxY(aabb.maxY + (mc.player.getDistanceSq(blockPos) < 10 ? 0 : 3)).grow(0.0020000000949949026D).offset(-interp2.x, -interp2.y, -interp2.z);
        GlStateManager.enableCull();
        RuniteTessellator.drawIndicator(aabb, new Color(red, green, blue, alpha.getValue()).getRGB(), 63);
    }

    private void drawBlockOCUB(final BlockPos blockPos, final int r, final int g, final int b, final Pair<Boolean, Boolean> pair) {
        final int red = (future.getValue() && (!pair.getKey() && pair.getValue()) ? 255 : r);
        final int green = (future.getValue() && (!pair.getKey() && pair.getValue()) ? 255 : g);
        final int blue = (future.getValue() && (!pair.getKey() && pair.getValue()) ? 0 : b);
        final IBlockState iBlockState2 = mc.world.getBlockState(blockPos);
        final Vec3d interp2 = MathUtil.interpolateEntity(mc.player, mc.getRenderPartialTicks());
        AxisAlignedBB aabb = iBlockState2.getSelectedBoundingBox(mc.world, blockPos);
        aabb = aabb.setMaxY(aabb.maxY - 1 * cuboid.getValue()).grow(0.0020000000949949026D).offset(-interp2.x, -interp2.y, -interp2.z);
        RuniteTessellator.drawBoundingBox(aabb, 1.5f, red, green, blue, alpha2.getValue());
    }
    private void drawBlockOF(final BlockPos blockPos, final int r, final int g, final int b, final Pair<Boolean, Boolean> pair) {
        final int red = (future.getValue() && (!pair.getKey() && pair.getValue()) ? 255 : r);
        final int green = (future.getValue() && (!pair.getKey() && pair.getValue()) ? 255 : g);
        final int blue = (future.getValue() && (!pair.getKey() && pair.getValue()) ? 0 : b);
        final IBlockState iBlockState = mc.world.getBlockState(blockPos);
        final Vec3d interp = MathUtil.interpolateEntity(mc.player, mc.getRenderPartialTicks());
        RuniteTessellator.drawBoundingBoxFace(iBlockState.getSelectedBoundingBox(mc.world, blockPos).grow(0.0020000000949949026D).offset(-interp.x, -interp.y, -interp.z), 1.5f, red, green, blue, alpha2.getValue());
    }

    @Override
    public String getHudInfo() {
        int holes = 0;
        if (safeHoles != null) holes = safeHoles.size();
        return "[" + ChatFormatting.WHITE + holes + ChatFormatting.RESET + "]";
    }
}
