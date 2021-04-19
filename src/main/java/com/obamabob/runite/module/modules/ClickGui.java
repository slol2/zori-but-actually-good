package com.obamabob.runite.module.modules;

import com.obamabob.runite.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import org.lwjgl.input.Keyboard;

public class ClickGui extends Module {
    public ClickGui() {
        super("ClickGui", Category.RENDER);
        setBind(Keyboard.KEY_RSHIFT);
    }

    @Override
    public void onEnable() {
        Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
        Minecraft.getMinecraft().displayGuiScreen(com.obamabob.runite.clickgui.ClickGui.getClickGui());
        disable();
    }
}
