package com.obamabob.runite.settings;

import com.obamabob.runite.module.Module;

import java.util.ArrayList;

public class SettingManager {
    private ArrayList<Setting> settings;

    public SettingManager() {
        this.settings = new ArrayList<>();
    }

    public void register(Setting setting) {
        this.settings.add(setting);
    }

    public ArrayList<Setting> getSettings() {
        return settings;
    }

    public ArrayList<Setting<?>> getSettingsByMod(Module mod){
        ArrayList<Setting<?>> out = new ArrayList<>();
        for(Setting<?> s : settings){
            if(s.parent.equals(mod)){
                out.add(s);
            }
        }
        return out;
    }

    public Setting<?> getSettingByMod(Module mod, String name){
        for (Setting setting : getSettingsByMod(mod)) {
            if(setting.getName().equalsIgnoreCase(name)) return setting;
        }
        return null;
    }
}
