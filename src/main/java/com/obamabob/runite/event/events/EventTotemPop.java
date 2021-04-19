package com.obamabob.runite.event.events;

import com.obamabob.runite.event.Event;
import me.zero.alpine.type.Cancellable;
import net.minecraft.entity.player.EntityPlayer;

public class EventTotemPop extends Event {
    EntityPlayer player;

    public EventTotemPop(EntityPlayer entityPlayerIn) {
        this.player = entityPlayerIn;
    }

    public EntityPlayer getPlayer() {
        return player;
    }
}