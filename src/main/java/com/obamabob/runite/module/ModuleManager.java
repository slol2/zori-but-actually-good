package com.obamabob.runite.module;

import com.obamabob.runite.module.modules.ClickGui;
import com.obamabob.runite.module.modules.client.HUD;
import com.obamabob.runite.module.modules.combat.*;
import com.obamabob.runite.module.modules.misc.*;
import com.obamabob.runite.module.modules.movement.Blink;
import com.obamabob.runite.module.modules.movement.Velocity;
import com.obamabob.runite.module.modules.render.*;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class ModuleManager {
    public static ArrayList<Module> modules;

    public ModuleManager() {
        modules = new ArrayList<>();
        modules.add(new ClickGui());
        modules.add(new PopCounter());
        modules.add(new DiscordRPC());
        modules.add(new FakePlayer());
        modules.add(new Timer());
        modules.add(new PearlNotify());
        modules.add(new MiddleClickFriend());
        modules.add(new Chams());
        modules.add(new ViewModel());
        modules.add(new BlockHighlight());
        modules.add(new HoleESP());
        modules.add(new SkyColour());
        modules.add(new AutoCrystal());
        modules.add(new NoRender());
        modules.add(new EnchantColor());
        modules.add(new Velocity());
        modules.add(new AutoTotem());
        modules.add(new PistonAura());
        modules.add(new AutoAnvil());
        modules.add(new BedAura());
        modules.add(new AutoBuilder());
        modules.add(new HoleFill());
        modules.add(new MiddleClickPearl());
        modules.add(new HUD());
        modules.add(new Speedmine());
        modules.add(new Skeleton());
        modules.add(new ShulkerPreview());
        modules.add(new Anti32k());
        modules.add(new EzLog());
        modules.add(new Nametags());
        modules.add(new Aura());
        modules.add(new Burrow());
        modules.add(new Surround());
        modules.add(new Blink());
    }

    public static Module getModuleByName(String name){
        Module m = modules.stream().filter(mm->mm.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
        return m;
    }

    public static boolean isModuleEnabled(String name){
        Module m = modules.stream().filter(mm->mm.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
        return m.isToggled();
    }

    public static ArrayList<Module> getModulesByCategory(Module.Category c){
        ArrayList<Module> list = (ArrayList<Module>) modules.stream().filter(m -> m.category.equals(c)).collect(Collectors.toList());
        return list;
    }

    public static void onBind(int key) {
        if (key == 0 || key == Keyboard.KEY_NONE) return;
        modules.forEach(module -> {
            if(module.getBind() == key){
                module.toggle();
            }
        });
    }

    public <T extends Module> T getModuleT(Class<T> clazz) {
        return modules.stream().filter(module -> module.getClass() == clazz).map(module -> (T) module).findFirst().orElse(null);
    }
}
