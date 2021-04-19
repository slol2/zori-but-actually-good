package com.obamabob.runite.clickgui;

import com.obamabob.runite.clickgui.item.Item;
import com.obamabob.runite.clickgui.item.ModuleButton;
import com.obamabob.runite.module.Module;
import com.obamabob.runite.module.ModuleManager;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Mouse;

public final class ClickGui extends GuiScreen
{
    private static ClickGui clickGui;
    private ArrayList<Snow> snowList = new ArrayList<Snow>();
    private final ArrayList<Panel> panels;
    
    public ClickGui() {
        Random random = new Random();
        for (int i = 0; i < 100; ++i)
        {
            for (int y = 0; y < 3; ++y)
            {
                Snow snow = new Snow(25 * i, y * -50, random.nextInt(3) + 1, random.nextInt(2)+1);
                snowList.add(snow);
            }
        }
        this.panels = new ArrayList<Panel>();
        if (this.getPanels().isEmpty()) {
            this.load();
        }
    }
    
    public static ClickGui getClickGui() {
        return (ClickGui.clickGui == null) ? (ClickGui.clickGui = new ClickGui()) : ClickGui.clickGui;
    }
    
    private void load() {
        int x = -84;
        for (final Module.Category c : Module.Category.values()) {
            final ArrayList<Panel> panels = this.panels;
            final String label = Character.toUpperCase(c.name().toLowerCase().charAt(0)) + c.name().toLowerCase().substring(1);
            x += 90;
            panels.add(new Panel(label, x, 4, true) {
                @Override
                public void setupItems() {
                    for (Module m : ModuleManager.modules) {
                        if (!m.getCategory().equals(c) || m.getName().equalsIgnoreCase("clickgui")) {
                            continue;
                        }
                        this.addButton(new ModuleButton(m, this));
                    }
                }
            });
        }
        this.panels.forEach(panel -> panel.getItems().sort((item1, item2) -> item1.getLabel().compareTo(item2.getLabel())));
    }
    
    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        this.drawDefaultBackground();
        final ScaledResolution res = new ScaledResolution(mc);
        if (!snowList.isEmpty())
        {
            snowList.forEach(snow -> snow.Update(res));
        }
        fontRenderer.drawString("Runite 1.2 by obamabob", res.getScaledWidth()-fontRenderer.getStringWidth("Runite 1.2 by obamabob")-2, res.getScaledHeight()-fontRenderer.FONT_HEIGHT-2, new Color((int)(Math.random() * 0x1000000)).getRGB());
        this.panels.forEach(panel -> panel.drawScreen(mouseX, mouseY, partialTicks));
    }
    
    public void mouseClicked(final int mouseX, final int mouseY, final int clickedButton) {
        this.panels.forEach(panel -> panel.mouseClicked(mouseX, mouseY, clickedButton));
    }

    @Override
    public void mouseReleased(final int mouseX, final int mouseY, final int releaseButton) {
        this.panels.forEach(panel -> panel.mouseReleased(mouseX, mouseY, releaseButton));
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        for (Panel p : panels) {
            if (p != null && p.getOpen()) {
                for (Item e : p.getItems()) {
                    try {
                        if (e.keyTyped(typedChar, keyCode))return;
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
        try {
            super.keyTyped(typedChar, keyCode);
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    public void handleMouseInput() throws IOException {
        int scrollAmount = 5;
        if (Mouse.getEventDWheel() > 0) {
            for (Panel p : panels) {
                p.setY(p.getY() + scrollAmount);
            }
        }
        if (Mouse.getEventDWheel() < 0) {
            for (Panel p : panels) {
                p.setY(p.getY() - scrollAmount);
            }
        }

        super.handleMouseInput();
    }
    
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
    
    public final ArrayList<Panel> getPanels() {
        return this.panels;
    }
}
