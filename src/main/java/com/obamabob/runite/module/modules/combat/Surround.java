package com.obamabob.runite.module.modules.combat;

import com.obamabob.runite.module.Module;
import com.obamabob.runite.settings.Setting;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Surround extends Module {
    public Surround() {super("Surround", Category.COMBAT);}

    private List<Block> whiteList = Arrays.asList(Blocks.OBSIDIAN, Blocks.ENDER_CHEST);

    Setting<Boolean> center = register(new Setting<>("Center", this, true));
    Setting<Boolean> rotate = register(new Setting<>("Rotate", this, true));

    @Override
    public void onEnable() {
        if (center.getValue()) Minecraft.getMinecraft().player.setPosition(Math.floor(Minecraft.getMinecraft().player.posX)+.5, Math.floor(Minecraft.getMinecraft().player.posY), Math.floor(Minecraft.getMinecraft().player.posZ)+.5);
    }

    public static boolean hasNeighbour(BlockPos blockPos) {
        for (EnumFacing side : EnumFacing.values()) {
            BlockPos neighbour = blockPos.offset(side);
            if(!Minecraft.getMinecraft().world.getBlockState(neighbour).getMaterial().isReplaceable())
                return true;
        }
        return false;
    }

    public void onTick() {
        if(!Minecraft.getMinecraft().player.onGround) disable();
        if (!isToggled() || Minecraft.getMinecraft().player == null) disable();
        Vec3d vec3d = getInterpolatedPos(Minecraft.getMinecraft().player, 0);
        BlockPos northBlockPos = new BlockPos(vec3d).north();
        BlockPos southBlockPos = new BlockPos(vec3d).south();
        BlockPos eastBlockPos = new BlockPos(vec3d).east();
        BlockPos westBlockPos = new BlockPos(vec3d).west();

        // //check if block is already placed
        // if(!Wrapper.getWorld().getBlockState(northBlockPos).getMaterial().isReplaceable() || !Wrapper.getWorld().getBlockState(southBlockPos).getMaterial().isReplaceable() || !Wrapper.getWorld().getBlockState(eastBlockPos).getMaterial().isReplaceable() || !Wrapper.getWorld().getBlockState(westBlockPos).getMaterial().isReplaceable())
        //     return;
        // search blocks in hotbar
        int newSlot = -1;
        for(int i = 0; i < 9; i++)
        {
            // filter out non-block items
            ItemStack stack =
                    Minecraft.getMinecraft().player.inventory.getStackInSlot(i);

            if(stack == ItemStack.EMPTY || !(stack.getItem() instanceof ItemBlock)) {
                continue;
            }
            // only use whitelisted blocks
            Block block = ((ItemBlock) stack.getItem()).getBlock();
            if (!whiteList.contains(block)) {
                continue;
            }

            newSlot = i;
            break;
        }

        // check if any blocks were found
        if(newSlot == -1)
            return;

        // set slot
        int oldSlot = Minecraft.getMinecraft().player.inventory.currentItem;
        Minecraft.getMinecraft().player.inventory.currentItem = newSlot;

        // check if we don't have a block adjacent to North blockpos
        A: if (!hasNeighbour(northBlockPos)) {
            // find air adjacent to blockpos that does have a block adjacent to it, let's fill this first as to form a bridge between the player and the original blockpos. necessary if the player is going diagonal.
            for (EnumFacing side : EnumFacing.values()) {
                BlockPos neighbour = northBlockPos.offset(side);
                if (hasNeighbour(neighbour)) {
                    northBlockPos = neighbour;
                    break A;
                }
            }
            return;
        }

        // check if we don't have a block adjacent to South blockpos
        B: if (!hasNeighbour(southBlockPos)) {
            // find air adjacent to blockpos that does have a block adjacent to it, let's fill this first as to form a bridge between the player and the original blockpos. necessary if the player is going diagonal.
            for (EnumFacing side : EnumFacing.values()) {
                BlockPos neighbour = southBlockPos.offset(side);
                if (hasNeighbour(neighbour)) {
                    southBlockPos = neighbour;
                    break B;
                }
            }
            return;
        }

        // check if we don't have a block adjacent to East blockpos
        C: if (!hasNeighbour(eastBlockPos)) {
            // find air adjacent to blockpos that does have a block adjacent to it, let's fill this first as to form a bridge between the player and the original blockpos. necessary if the player is going diagonal.
            for (EnumFacing side : EnumFacing.values()) {
                BlockPos neighbour = eastBlockPos.offset(side);
                if (hasNeighbour(neighbour)) {
                    eastBlockPos = neighbour;
                    break C;
                }
            }
            return;
        }

        // check if we don't have a block adjacent to West blockpos
        D: if (!hasNeighbour(westBlockPos)) {
            // find air adjacent to blockpos that does have a block adjacent to it, let's fill this first as to form a bridge between the player and the original blockpos. necessary if the player is going diagonal.
            for (EnumFacing side : EnumFacing.values()) {
                BlockPos neighbour = westBlockPos.offset(side);
                if (hasNeighbour(neighbour)) {
                    westBlockPos = neighbour;
                    break D;
                }
            }
            return;
        }



        // place blocks
        if(Minecraft.getMinecraft().world.getBlockState(northBlockPos).getMaterial().isReplaceable() && isEntitiesEmpty(northBlockPos))
            placeBlockScaffold(northBlockPos, rotate.getValue());

        if(Minecraft.getMinecraft().world.getBlockState(southBlockPos).getMaterial().isReplaceable() && isEntitiesEmpty(southBlockPos))
            placeBlockScaffold(southBlockPos, rotate.getValue());

        if(Minecraft.getMinecraft().world.getBlockState(eastBlockPos).getMaterial().isReplaceable() && isEntitiesEmpty(eastBlockPos))
            placeBlockScaffold(eastBlockPos, rotate.getValue());

        if(Minecraft.getMinecraft().world.getBlockState(westBlockPos).getMaterial().isReplaceable() && isEntitiesEmpty(westBlockPos))
            placeBlockScaffold(westBlockPos, rotate.getValue());

        // reset slot
        Minecraft.getMinecraft().player.inventory.currentItem = oldSlot;
    }

    private boolean isEntitiesEmpty(BlockPos pos){
        List<Entity> entities =  Minecraft.getMinecraft().world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos)).stream()
                .filter(e -> !(e instanceof EntityItem))
                .filter(e -> !(e instanceof EntityXPOrb))
                .collect(Collectors.toList());
        return entities.isEmpty();
    }

    public static boolean placeBlockScaffold(BlockPos pos, boolean rotate) {
        Vec3d eyesPos = new Vec3d(Minecraft.getMinecraft().player.posX,
                Minecraft.getMinecraft().player.posY + Minecraft.getMinecraft().player.getEyeHeight(),
                Minecraft.getMinecraft().player.posZ);

        for(EnumFacing side : EnumFacing.values())
        {
            BlockPos neighbor = pos.offset(side);
            EnumFacing side2 = side.getOpposite();

            // check if side is visible (facing away from player)
            //if(eyesPos.squareDistanceTo(
            //        new Vec3d(pos).add(0.5, 0.5, 0.5)) >= eyesPos
            //        .squareDistanceTo(
            //                new Vec3d(neighbor).add(0.5, 0.5, 0.5)))
            //    continue;

            // check if neighbor can be right clicked
            if(!canBeClicked(neighbor))
                continue;

            Vec3d hitVec = new Vec3d(neighbor).add(0.5, 0.5, 0.5)
                    .add(new Vec3d(side2.getDirectionVec()).scale(0.5));

            // check if hitVec is within range (4.25 blocks)
            //if(eyesPos.squareDistanceTo(hitVec) > 18.0625)
            //continue;

            // place block
            if(rotate)
                faceVectorPacketInstant(hitVec);
            Minecraft.getMinecraft().player.connection.sendPacket(new CPacketEntityAction(Minecraft.getMinecraft().player, CPacketEntityAction.Action.START_SNEAKING));
            processRightClickBlock(neighbor, side2, hitVec);
            Minecraft.getMinecraft().player.swingArm(EnumHand.MAIN_HAND);
            Minecraft.getMinecraft().rightClickDelayTimer = 0;
            Minecraft.getMinecraft().player.connection.sendPacket(new CPacketEntityAction(Minecraft.getMinecraft().player, CPacketEntityAction.Action.STOP_SNEAKING));

            return true;
        }

        return false;
    }

    private static PlayerControllerMP getPlayerController()
    {
        return Minecraft.getMinecraft().playerController;
    }

    public static void processRightClickBlock(BlockPos pos, EnumFacing side,
                                              Vec3d hitVec)
    {
        getPlayerController().processRightClickBlock(Minecraft.getMinecraft().player,
                Minecraft.getMinecraft().world, pos, side, hitVec, EnumHand.MAIN_HAND);
    }

    public static IBlockState getState(BlockPos pos)
    {
        return Minecraft.getMinecraft().world.getBlockState(pos);
    }

    public static Block getBlock(BlockPos pos)
    {
        return getState(pos).getBlock();
    }

    public static boolean canBeClicked(BlockPos pos)
    {
        return getBlock(pos).canCollideCheck(getState(pos), false);
    }

    public static void faceVectorPacketInstant(Vec3d vec)
    {
        float[] rotations = getNeededRotations2(vec);

        Minecraft.getMinecraft().player.connection.sendPacket(new CPacketPlayer.Rotation(rotations[0],
                rotations[1], Minecraft.getMinecraft().player.onGround));
    }

    private static float[] getNeededRotations2(Vec3d vec)
    {
        Vec3d eyesPos = getEyesPos();

        double diffX = vec.x - eyesPos.x;
        double diffY = vec.y - eyesPos.y;
        double diffZ = vec.z - eyesPos.z;

        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
        float pitch = (float)-Math.toDegrees(Math.atan2(diffY, diffXZ));

        return new float[]{
                Minecraft.getMinecraft().player.rotationYaw
                        + MathHelper.wrapDegrees(yaw - Minecraft.getMinecraft().player.rotationYaw),
                Minecraft.getMinecraft().player.rotationPitch + MathHelper
                        .wrapDegrees(pitch - Minecraft.getMinecraft().player.rotationPitch)};
    }

    public static Vec3d getEyesPos()
    {
        return new Vec3d(Minecraft.getMinecraft().player.posX,
                Minecraft.getMinecraft().player.posY + Minecraft.getMinecraft().player.getEyeHeight(),
                Minecraft.getMinecraft().player.posZ);
    }

    public static Vec3d getInterpolatedPos(Entity entity, float ticks) {
        return new Vec3d(entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ).add(getInterpolatedAmount(entity, ticks));
    }

    public static Vec3d getInterpolatedAmount(Entity entity, double ticks) {
        return getInterpolatedAmount(entity, ticks, ticks, ticks);
    }

    public static Vec3d getInterpolatedAmount(Entity entity, double x, double y, double z) {
        return new Vec3d(
                (entity.posX - entity.lastTickPosX) * x,
                (entity.posY - entity.lastTickPosY) * y,
                (entity.posZ - entity.lastTickPosZ) * z
        );
    }
}