package com.obamabob.runite.event.events;

import com.obamabob.runite.event.Event;
import net.minecraft.util.EnumHandSide;

public class EventTransformSideFirstPerson extends Event {

    private final EnumHandSide enumHandSide;

    public EventTransformSideFirstPerson(EnumHandSide enumHandSide){
        this.enumHandSide = enumHandSide;
    }

    public EnumHandSide getEnumHandSide(){
        return this.enumHandSide;
    }
}