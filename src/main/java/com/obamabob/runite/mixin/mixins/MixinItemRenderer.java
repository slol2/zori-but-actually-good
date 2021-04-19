package com.obamabob.runite.mixin.mixins;

import com.obamabob.runite.Runite;
import com.obamabob.runite.event.events.EventTransformSideFirstPerson;
import com.obamabob.runite.module.ModuleManager;
import com.obamabob.runite.module.modules.render.ViewModel;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ItemRenderer.class)
public class MixinItemRenderer {

    @Inject(method = "transformSideFirstPerson", at = @At("HEAD"))
    public void transformSideFirstPerson(EnumHandSide hand, float p_187459_2_, CallbackInfo callbackInfo) {
        EventTransformSideFirstPerson event = new EventTransformSideFirstPerson(hand);
        event.call();
    }

    @Inject(method = "transformEatFirstPerson", at = @At("HEAD"), cancellable = true)
    public void transformEatFirstPerson(float p_187454_1_, EnumHandSide hand, ItemStack stack, CallbackInfo callbackInfo) {
        EventTransformSideFirstPerson event = new EventTransformSideFirstPerson(hand);
        event.call();
        if (ModuleManager.isModuleEnabled("ViewModel") && ((ViewModel) ModuleManager.getModuleByName("ViewModel")).cancelEating.getValue()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = "transformFirstPerson", at = @At("HEAD"))
    public void transformFirstPerson(EnumHandSide hand, float p_187453_2_, CallbackInfo callbackInfo) {
        EventTransformSideFirstPerson event = new EventTransformSideFirstPerson(hand);
        event.call();
    }
}