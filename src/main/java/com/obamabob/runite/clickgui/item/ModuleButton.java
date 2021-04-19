package com.obamabob.runite.clickgui.item;

import com.obamabob.runite.Runite;
import com.obamabob.runite.clickgui.Panel;
import com.obamabob.runite.clickgui.item.properties.*;
import com.obamabob.runite.module.Module;
import com.obamabob.runite.settings.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.init.SoundEvents;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ModuleButton extends Button
{
    Minecraft mc = Minecraft.getMinecraft();
    FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;

    private final Module module;
    private List<Item> items;
    private boolean subOpen;
    
    public ModuleButton(final Module module, final Panel parent) {
        super(module.getName(), parent);
        this.items = new ArrayList<Item>();
        this.module = module;
        if (Runite.getInstance().settingManager.getSettingsByMod(module) != null) {
            for (Setting s : Runite.getInstance().settingManager.getSettingsByMod(module)) {
                if (s.isToggle()) {
                    items.add(new BooleanButton(s));
                } else if (s.isNumber()) {
                    items.add(new NumberSlider(s));
                } else if (s.isMode()) {
                    items.add(new ModeButton(s));
                } else if (s.isEnum()) {
                    items.add(new EnumButton(s));
                } else if (s.isBind()) {
                    items.add(new BindButton(s));
                }
            }
        }
    }
    
    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (!this.items.isEmpty()) {
            if (Runite.getInstance().settingManager.getSettingsByMod(module) != null)
                fontRenderer.drawStringWithShadow("...", this.x + this.width - (fontRenderer.getStringWidth("...")) - 2, this.y + 4.0f, -1);
            if (this.subOpen) {
                float height = 1.0f;
                for (final Item item : this.items) {
                    if (!item.property.isVisible()) continue;
                    height += 15.0f;
                    item.setLocation(this.x + 1.0f, this.y + height);
                    item.setHeight(15);
                    item.setWidth(this.width - 9);
                    item.drawScreen(mouseX, mouseY, partialTicks);
                }
            }
        }
    }
    
    @Override
    public void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (!this.items.isEmpty()) {
            if (mouseButton == 1 && this.isHovering(mouseX, mouseY)) {
                this.subOpen = !this.subOpen;
                mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            }
            if (this.subOpen) {
                for (final Item item : this.items) {
                    if (!item.property.isVisible()) continue;
                    item.mouseClicked(mouseX, mouseY, mouseButton);
                }
            }
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int releaseButton) {
        items.forEach(item -> {
            if (item.property.isVisible()) {
                item.mouseReleased(mouseX, mouseY, releaseButton);
            }
        });
        super.mouseReleased(mouseX, mouseY, releaseButton);
    }

    @Override
    public boolean keyTyped(char typedChar, int keyCode) throws IOException {
        for (Item i : items) {
            if (!i.property.isVisible()) continue;
            try {
                if (i.keyTyped(typedChar, keyCode)) return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return super.keyTyped(typedChar, keyCode);
    }

    @Override
    public int getHeight() {
        if (this.subOpen) {
            int height = 14;
            for (final Item item : this.items) {
                if (!item.property.isVisible()) continue;
                height += item.getHeight() + 1;
            }
            return height + 2;
        }
        return 14;
    }
    
    @Override
    public void toggle() {
        module.toggle();
    }

    @Override
    public boolean getState () {
        return module.isToggled();
    }
}
