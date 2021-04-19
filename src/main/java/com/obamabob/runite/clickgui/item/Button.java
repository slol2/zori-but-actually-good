package com.obamabob.runite.clickgui.item;

import com.obamabob.runite.clickgui.ClickGui;
import com.obamabob.runite.clickgui.Labeled;
import com.obamabob.runite.clickgui.Panel;
import com.obamabob.runite.util.ColorUtil;
import com.obamabob.runite.util.RuniteTessellator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.init.SoundEvents;

public class Button extends Item implements Labeled
{
    Minecraft mc = Minecraft.getMinecraft();
    FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;

    private Panel parent;
    private boolean state;
    
    public Button(final String label, final Panel parent) {
        super(label);
        this.parent = parent;
        this.height = 15;
    }
    
    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        //RenderMethods.drawGradientRect(this.x, this.y, this.x + this.width, this.y + this.height, this.getState() ? (this.isHovering(mouseX, mouseY) ? 1442529858 : 2012955202) : (this.isHovering(mouseX, mouseY) ? -2007673515 : 861230421), this.getState() ? (this.isHovering(mouseX, mouseY) ? -1711586750 : -1426374078) : (this.isHovering(mouseX, mouseY) ? -1722460843 : 1431655765));
        //RuniteTessellator.drawRectGradient(this.x, this.y, this.x + this.width, this.y + this.height, this.getState() ? (this.isHovering(mouseX, mouseY) ? 1442529858 : 2012955202) : (this.isHovering(mouseX, mouseY) ? -2007673515 : 861230421), this.getState() ? (this.isHovering(mouseX, mouseY) ? -1711586750 : -1426374078) : (this.isHovering(mouseX, mouseY) ? -1722460843 : 1431655765));
        RuniteTessellator.drawRectGradient(this.x, this.y, this.x + this.width, this.y + this.height, this.getState() ? ColorUtil.changeAlpha(parent.rgb, 225) : 861230421, -1);
        if (this.isHovering(mouseX, mouseY)) {
            if (this.getState()) {
                RuniteTessellator.drawRectGradient(this.x, this.y, this.x + this.width, this.y + this.height, ColorUtil.changeAlpha(ColorUtil.Colors.BLACK, 30), -1);
            } else {
                RuniteTessellator.drawRectGradient(this.x, this.y, this.x + this.width, this.y + this.height, ColorUtil.changeAlpha(ColorUtil.Colors.WHITE, 30), -1);
            }
        }
        fontRenderer.drawStringWithShadow(this.getLabel(), this.x + 2.3f, this.y + 4.0f, this.getState() ? -1 : -5592406);
    }
    
    @Override
    public void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        if (mouseButton == 0 && this.isHovering(mouseX, mouseY)) {
            this.state = !this.state;
            this.toggle();
            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
        }
    }
    
    public void toggle() {
    }
    
    public boolean getState() {
        return this.state;
    }
    
    @Override
    public int getHeight() {
        return 14;
    }
    
    protected boolean isHovering(final int mouseX, final int mouseY) {
        for (final Panel panel : ClickGui.getClickGui().getPanels()) {
            if (panel.drag) {
                return false;
            }
        }
        return mouseX >= this.getX() && mouseX <= this.getX() + this.getWidth() && mouseY >= this.getY() && mouseY <= this.getY() + this.height;
    }
}
