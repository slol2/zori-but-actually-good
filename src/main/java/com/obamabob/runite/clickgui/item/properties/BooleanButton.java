package com.obamabob.runite.clickgui.item.properties;

import com.obamabob.runite.Runite;
import com.obamabob.runite.clickgui.item.Button;
import com.obamabob.runite.settings.Setting;
import com.obamabob.runite.util.ColorUtil;
import com.obamabob.runite.util.RuniteTessellator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.init.SoundEvents;

public class BooleanButton extends Button
{
    Minecraft mc = Minecraft.getMinecraft();
    FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
    
    public BooleanButton(final Setting property) {
        super(property.getName(), null);
        setValue(property);
        this.width = 15;
    }
    
    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        //RuniteTessellator.drawRectDouble(this.x, this.y, this.x + this.width + 7.4f, this.y + this.height, this.getState() ? (this.isHovering(mouseX, mouseY) ? -1711586750 : 2012955202) : (this.isHovering(mouseX, mouseY) ? -2007673515 : 290805077));
        RuniteTessellator.drawRectGradient(this.x, this.y, this.x + this.width + 7.4f, this.y + this.height, this.getState() ? ColorUtil.changeAlpha(Runite.rgb, 200) : 290805077, -1);
        if (this.isHovering(mouseX, mouseY)) {
            if (this.getState()) {
                RuniteTessellator.drawRectGradient(this.x, this.y, this.x + this.width, this.y + this.height, ColorUtil.changeAlpha(ColorUtil.Colors.BLACK, 25), -1);
            } else {
                RuniteTessellator.drawRectGradient(this.x, this.y, this.x + this.width, this.y + this.height, ColorUtil.changeAlpha(ColorUtil.Colors.WHITE, 25), -1);
            }
        }
        fontRenderer.drawStringWithShadow(this.getLabel(), this.x + 2.3f, this.y + 4.0f, this.getState() ? -1 : -5592406);
    }
    
    @Override
    public void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.isHovering(mouseX, mouseY)) {
            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
        }
    }
    
    @Override
    public int getHeight() {
        return 14;
    }
    
    @Override
    public void toggle() {
        this.property.setValue(!((boolean)this.property.getValue()));
    }
    
    @Override
    public boolean getState() {
        return (boolean) this.property.getValue();
    }
}
