package com.obamabob.runite.module.modules.render;

import com.obamabob.runite.Runite;
import com.obamabob.runite.event.events.EventRender;
import com.obamabob.runite.module.Module;
import com.obamabob.runite.settings.Setting;
import com.obamabob.runite.util.MathUtil;
import com.obamabob.runite.util.RuniteTessellator;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameType;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

public class BlockHighlight extends Module {
    public BlockHighlight() {
        super("BlockHighlight", Category.RENDER);
    }

    private static BlockPos position;

    Setting<Integer> red = register(new Setting<>("Red", this, 255, 0 , 255));
    Setting<Integer> green = register(new Setting<>("Green", this, 0, 0 , 255));
    Setting<Integer> blue = register(new Setting<>("Blue", this, 0, 0 , 255));
    Setting<Boolean> rainbow = register(new Setting<>("Rainbow", this, true));
    Setting<Integer> alpha = register(new Setting<>("Alpha", this, 255, 0 , 255));
    Setting<Integer> width = register(new Setting<>("Width", this, 3, 0 , 10));

    @Override
    public void onEnable() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void onDisable() {
        MinecraftForge.EVENT_BUS.unregister(this);
        position = null;
    }

    @Override
    public void onWorldRender(EventRender event) {
        final Minecraft mc = Minecraft.getMinecraft();
        final RayTraceResult ray = mc.objectMouseOver;
        if (ray != null) {
            if (ray.typeOfHit == RayTraceResult.Type.BLOCK) {

                final BlockPos blockpos = ray.getBlockPos();
                final IBlockState iblockstate = mc.world.getBlockState(blockpos);

                if (iblockstate.getMaterial() != Material.AIR && mc.world.getWorldBorder().contains(blockpos)) {
                    final Vec3d interp = MathUtil.interpolateEntity(mc.player, mc.getRenderPartialTicks());
                    int r = red.getValue();
                    int g = green.getValue();
                    int b = blue.getValue();
                    if (rainbow.getValue()) RuniteTessellator.drawBoundingBox(iblockstate.getSelectedBoundingBox(mc.world, blockpos).grow(0.0020000000949949026D).offset(-interp.x, -interp.y, -interp.z), width.getValue(), new Color(Runite.rgb).getRed(), new Color(Runite.rgb).getGreen(), new Color(Runite.rgb).getBlue(), alpha.getValue());
                    else RuniteTessellator.drawBoundingBox(iblockstate.getSelectedBoundingBox(mc.world, blockpos).grow(0.0020000000949949026D).offset(-interp.x, -interp.y, -interp.z), width.getValue(), r, g, b, alpha.getValue());
                }
            }
        }
    }

    @SubscribeEvent
    public void onDrawBlockHighlight(DrawBlockHighlightEvent event) {
        if ((mc.player == null) || (mc.world == null)
                || ((!mc.playerController.getCurrentGameType().equals(GameType.SURVIVAL))
                && (!mc.playerController.getCurrentGameType().equals(GameType.CREATIVE)))) {
            return;
        }
        event.setCanceled(true);
    }
}