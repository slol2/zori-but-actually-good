package com.obamabob.runite.mixin.mixins;

import com.obamabob.runite.event.events.EventMiddleClick;
import com.obamabob.runite.util.FileManager;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    @Inject(method = "middleClickMouse", at = @At("HEAD"))
    private void middleClickMouse(CallbackInfo callback) {
        new EventMiddleClick().call();
    }

    @Inject(method = "shutdownMinecraftApplet", at = @At("HEAD"))
    private void stopClient(CallbackInfo callbackInfo) {
        FileManager fileManager = new FileManager();
        fileManager.saveBinds();
        fileManager.saveFriends();
        fileManager.saveHacks();
        fileManager.saveBinds();
        fileManager.savePrefix();
        fileManager.saveDrawn();
        fileManager.saveSettingsList();
    }
}