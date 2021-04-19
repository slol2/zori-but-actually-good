package com.obamabob.runite.module;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.obamabob.runite.Runite;
import com.obamabob.runite.command.Command;
import com.obamabob.runite.event.EventManager;
import com.obamabob.runite.event.events.EventRender;
import com.obamabob.runite.settings.Bind;
import com.obamabob.runite.settings.Setting;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

public class Module {
    protected final Minecraft mc = Minecraft.getMinecraft();
    protected void sendChatMessage(String message) {Command.sendMessage(message);}

    String name;
    Category category;
    public Setting<Bind> bind = register(new Setting<>("Bind", this, new Bind(Keyboard.KEY_NONE)));
    boolean toggled, drawn;

    public Module(String name, Category category) {
        this.name = name;
        this.category = category;
        bind.getValue().setNum(0);
        this.toggled = false;
        this.drawn = true;
    }

    public void onEnable(){}
    public void onDisable(){}
    public void onTick() {}
    public void onWorldRender(EventRender event) {}

    public String getName() {return name;}
    public Category getCategory() {return category;}
    public int getBind() {return bind.getValue().getNum();}
    public boolean isToggled() {return toggled;}
    public boolean isDrawn() {return drawn;}

    public String getHudInfo() {return "";}

    public void setBind(int bind) {this.bind.getValue().setNum(bind);}
    public void setToggled(boolean toggled) {this.toggled = toggled;}
    public void setDrawn(boolean drawn) {this.drawn = drawn;}

    public <T> Setting<T> register(Setting<T> setting){Runite.getInstance().settingManager.register(setting); return setting;}

    public void enable() {setToggled(true); EventManager.register(this); Command.sendMessage(name + ChatFormatting.GREEN + " enabled."); onEnable();}
    public void disable() {setToggled(false); EventManager.unregister(this); Command.sendMessage(name + ChatFormatting.RED + " disabled."); onDisable();}
    public void toggle() {if (toggled) disable(); else enable();}

    public enum Category {COMBAT, MISCELLANEOUS, MOVEMENT, RENDER, CLIENT}
}
