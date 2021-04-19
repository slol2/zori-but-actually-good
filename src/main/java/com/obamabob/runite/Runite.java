package com.obamabob.runite;

import com.obamabob.runite.clickgui.ClickGui;
import com.obamabob.runite.command.Command;
import com.obamabob.runite.command.CommandManager;
import com.obamabob.runite.event.EventHandler;
import com.obamabob.runite.module.Module;
import com.obamabob.runite.module.ModuleManager;
import com.obamabob.runite.settings.SettingManager;
import com.obamabob.runite.util.FileManager;
import com.obamabob.runite.util.TitleUtil;
import me.zero.alpine.EventBus;
import me.zero.alpine.EventManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

import java.awt.*;
import java.util.Comparator;

@Mod(modid = "runite", name = "Runite", version = "1.2")
public class Runite {
    public EventHandler eventHandler;
    public SettingManager settingManager;
    public ModuleManager moduleManager;
    public CommandManager commandManager;
    public static int rgb;

    @Mod.EventHandler
    public void innit(FMLInitializationEvent event) {
        // british people be like innit
        MinecraftForge.EVENT_BUS.register(new TitleUtil());
        eventHandler = new EventHandler();
        settingManager = new SettingManager();
        moduleManager = new ModuleManager();
        ModuleManager.modules.sort(Comparator.comparing(Module::getName));
        commandManager = new CommandManager();
        CommandManager.commands.sort(new Comparator<Command>() {
            @Override
            public int compare(final Command object1, final Command object2) {
                return object1.name[0].compareTo(object2.name[0]);
            }
        });

        FileManager fileManager = new FileManager();
        fileManager.loadBinds();
        fileManager.loadFriends();
        fileManager.loadHacks();
        fileManager.loadBinds();
        fileManager.loadPrefix();
        fileManager.loadDrawn();
        fileManager.loadSettingsList();
    }

    @Mod.Instance
    private static Runite INSTANCE;

    public Runite(){
        INSTANCE = this;
    }

    public static Runite getInstance(){
        return INSTANCE;
    }

    public static String getTitle(String in) {
        in = Character.toUpperCase(in.toLowerCase().charAt(0)) + in.toLowerCase().substring(1);
        return in;
    }

    public static class Rainbow {
        public static int rgb;
        public static int a;
        public static int r;
        public static int g;
        public static int b;
        static float hue = 0.01f;

        public static void updateRainbow() {
            rgb = Color.HSBtoRGB(hue, 1, 1);
            a = (rgb >>> 24) & 0xFF;
            r = (rgb >>> 16) & 0xFF;
            g = (rgb >>> 8) & 0xFF;
            b = rgb & 0xFF;
            hue += 0.00005;
            if (hue > 1) hue -= 1;
        }
    }
}
