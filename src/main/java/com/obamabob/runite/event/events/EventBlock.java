package com.obamabob.runite.event.events;

import com.obamabob.runite.event.Event;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class EventBlock extends Event {
    public BlockPos pos;
    public EnumFacing facing;

    private int stage;

    public EventBlock(final int stage, final BlockPos pos, final EnumFacing facing) {
        this.pos = pos;
        this.facing = facing;
        this.stage = stage;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }

    public int getStage() {
        return this.stage;
    }
}
