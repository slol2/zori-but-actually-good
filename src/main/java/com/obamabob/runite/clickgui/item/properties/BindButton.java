package com.obamabob.runite.clickgui.item.properties;

import com.obamabob.runite.Runite;
import com.obamabob.runite.clickgui.item.Button;
import com.obamabob.runite.settings.Bind;
import com.obamabob.runite.settings.Setting;
import com.obamabob.runite.util.ColorUtil;
import com.obamabob.runite.util.RuniteTessellator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.init.SoundEvents;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class BindButton extends Button {
    Minecraft mc = Minecraft.getMinecraft();
    FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;

    private boolean listening;

    public BindButton(final Setting property) {
        super(property.getName(), null);
        setValue(property);
    }

    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        //RuniteTessellator.drawRectDouble(this.x, this.y, this.x + this.width + 7.4f, this.y + this.height, this.getState() ? (this.isHovering(mouseX, mouseY) ? -1711586750 : 2012955202) : (this.isHovering(mouseX, mouseY) ? -2009910477 : 288568115));
        RuniteTessellator.drawRectGradient(this.x, this.y, this.x + this.width + 7.4f, this.y + this.height, this.getState() ? ColorUtil.changeAlpha(Runite.rgb, 200) : 290805077, -1);
        if (this.isHovering(mouseX, mouseY)) {
            if (this.getState()) {
                RuniteTessellator.drawRectGradient(this.x, this.y, this.x + this.width, this.y + this.height, ColorUtil.changeAlpha(ColorUtil.Colors.BLACK, 25), -1);
            } else {
                RuniteTessellator.drawRectGradient(this.x, this.y, this.x + this.width, this.y + this.height, ColorUtil.changeAlpha(ColorUtil.Colors.WHITE, 25), -1);
            }
        }
        String s = listening ? "..." : Keyboard.getKeyName(property.getParentMod().getBind());
        fontRenderer.drawStringWithShadow(String.format("%s\u00A77 %s", this.getLabel(), s), this.x + 2.3f, this.y + 4.0f, this.getState() ? -1 : -1);
    }

    @Override
    public void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.isHovering(mouseX, mouseY)) {
            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            if (mouseButton == 0) {
                listening = true;
            }
        }
    }

    @Override
    public boolean keyTyped(char typedChar, int keyCode) throws IOException {
        if (listening) {
            if (keyCode != Keyboard.KEY_ESCAPE) {
                property.getParentMod().setBind(keyCode);
            } else {
                property.getParentMod().setBind(Keyboard.KEY_NONE);
            }
            listening = false;
            return true;
        }
        return super.keyTyped(typedChar, keyCode);
    }

    @Override
    public int getHeight() {
        return 14;
    }

    @Override
    public void toggle() {
    }

    @Override
    public boolean getState() {
        return false;
    }
}

