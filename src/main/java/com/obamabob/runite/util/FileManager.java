package com.obamabob.runite.util;

import com.obamabob.runite.Runite;
import com.obamabob.runite.command.Command;
import com.obamabob.runite.friend.Friend;
import com.obamabob.runite.friend.Friends;
import com.obamabob.runite.module.Module;
import com.obamabob.runite.module.ModuleManager;
import com.obamabob.runite.settings.Setting;
import net.minecraft.client.Minecraft;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

public class FileManager {
    public File RuniteFile;
    public File RuniteSettings;

    public FileManager() {
        this.RuniteFile = new File(Minecraft.getMinecraft().gameDir + File.separator + "Runite");
        if (!this.RuniteFile.exists()) {
            this.RuniteFile.mkdirs();
        }

        this.RuniteSettings = new File(Minecraft.getMinecraft().gameDir + File.separator + "Runite" + File.separator + "Runite Settings");
        if (!this.RuniteSettings.exists()) {
            this.RuniteSettings.mkdirs();
        }
    }

    public void saveBinds() {
        try {
            File file = new File(this.RuniteFile.getAbsolutePath(), "Binds.txt");
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            for (Module module : ModuleManager.modules) {
                try {
                    out.write(module.getName() + ":" + module.getBind());
                    out.write("\r\n");
                } catch (Exception e) {
                    // empty
                }
            }
            out.close();
        } catch (Exception file) {
            // empty catch block
        }
    }

    public void loadBinds() {
        try {
            File file = new File(this.RuniteFile.getAbsolutePath(), "Binds.txt");
            FileInputStream fstream = new FileInputStream(file.getAbsolutePath());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    String curLine = line.trim();
                    String name = curLine.split(":")[0];
                    String bind = curLine.split(":")[1];
                    int b = Integer.parseInt(bind);
                    Module m = ModuleManager.getModuleByName(name);
                    if (m != null) {
                        m.setBind(b);
                    }
                } catch (Exception e) {
                    // empty
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
            saveBinds();
        }
    }

    public void saveHacks() {
        try {
            File file = new File(this.RuniteFile.getAbsolutePath(), "Modules.txt");
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            for (Module module : ModuleManager.modules) {
                try {
                    if (module.isToggled() && !module.getName().matches("null") && !module.getName().equals("Log Out Spot") && !module.getName().equals("Freecam") && !module.getName().equals("Blink") && !module.getName().equals("Join/Leave msgs") && !module.getName().equals("Elytra +") && !module.getName().equals("Sound")) {
                        out.write(module.getName());
                        out.write("\r\n");
                    }
                } catch (Exception e) {
                    // empty
                }
            }
            out.close();
        } catch (Exception file) {
            //
        }
    }

    public void saveFriends() {
        try {
            File file = new File(this.RuniteFile.getAbsolutePath(), "Friends.txt");
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            for (Friend f : Friends.getFriends()) {
                try {
                    out.write(f.getUsername());
                    out.write("\r\n");
                } catch (Exception e) {
                    // empty
                }
            }
            out.close();
        } catch (Exception file) {
            // empty catch block
        }
    }

    public void loadFriends() {
        try {
            File file = new File(this.RuniteFile.getAbsolutePath(), "Friends.txt");
            FileInputStream fstream = new FileInputStream(file.getAbsolutePath());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    Friends.addFriend(line);
                } catch (Exception e) {
                    // empty
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
            saveFriends();
        }
    }

    public void savePrefix() {
        try {
            File file = new File(this.RuniteFile.getAbsolutePath(), "Prefix.txt");
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            out.write(Command.prefix);
            out.write("\r\n");
            out.close();
        } catch (Exception file) {
            // empty catch block
        }
    }

    public void loadPrefix() {
        try {
            File file = new File(this.RuniteFile.getAbsolutePath(), "Prefix.txt");
            FileInputStream fstream = new FileInputStream(file.getAbsolutePath());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                Command.prefix = line;
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
            savePrefix();
        }
    }

    public void saveDrawn() {
        try {
            File file = new File(this.RuniteFile.getAbsolutePath(), "Drawn.txt");
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            for (Module module : ModuleManager.modules) {
                out.write(module.getName() + ":" + module.isDrawn());
                out.write("\r\n");
            }
            out.close();
        } catch (Exception file) {
            // empty catch block
        }
    }

    public void loadDrawn() {
        try {
            File file = new File(this.RuniteFile.getAbsolutePath(), "Drawn.txt");
            FileInputStream fstream = new FileInputStream(file.getAbsolutePath());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    String curLine = line.trim();
                    String name = curLine.split(":")[0];
                    String isOn = (curLine.split(":")[1]);
                    boolean drawn = Boolean.parseBoolean(isOn);
                    Module m = ModuleManager.getModuleByName(name);
                    m.setDrawn(drawn);
                } catch (Exception e) {
                    // empty
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
            saveDrawn();
        }
    }

    public void writeCrash(String alah) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("MM_dd_yyyy-HH_mm_ss");
            Date date = new Date();
            File file = new File(this.RuniteFile.getAbsolutePath(), "crashlog-".concat(format.format(date)).concat(".xen"));
            BufferedWriter outWrite = new BufferedWriter(new FileWriter(file));
            outWrite.write(alah);
            outWrite.close();
        } catch (Exception error) {
        }
    }

    public void loadHacks() {
        try {
            File file = new File(this.RuniteFile.getAbsolutePath(), "Modules.txt");
            FileInputStream fstream = new FileInputStream(file.getAbsolutePath());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                for (Module m : ModuleManager.modules) {
                    try {
                        if (m.getName().equals(line)) {
                            m.enable();
                        }
                    } catch (Exception e) {
                        // empty
                    }
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
            saveHacks();
        }
    }

    public String determineNumber(Object o) {
        if (o instanceof Integer) {
            return "INTEGER";
        } else if (o instanceof Float) {
            return "FLOAT";
        } else if (o instanceof Double) {
            return "DOUBLE";
        } else {
            return "INVALID";
        }
    }

    public void saveSettingsList() {
        //Slider
        try {
            File file = new File(RuniteSettings.getAbsolutePath(), "Slider.txt");
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            for (Setting<?> i : Runite.getInstance().settingManager.getSettings()) {

                if (i.isNumber()) {
                    out.write(i.getName() + ":" + i.getValue().toString() + ":" + i.getParentMod().getName() + "\r\n");
                }
            }
            out.close();
        } catch (Exception e) {
        }

        //Check
        try {
            File file = new File(RuniteSettings.getAbsolutePath(), "Check.txt");
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            for (Setting<?> i : Runite.getInstance().settingManager.getSettings()) {


                if (i.isToggle()) {
                    out.write(i.getName() + ":" + i.getValue().toString() + ":" + i.getParentMod().getName() + "\r\n");
                }
            }
            out.close();
        } catch (Exception e) {
        }

        //Combo
        try {
            File file = new File(RuniteSettings.getAbsolutePath(), "Combo.txt");
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            for (Setting<?> i : Runite.getInstance().settingManager.getSettings()) {
                if (i.isMode()) {
                    if (((Setting<String>) i).getValue().contains(":")) {
                        out.write(i.getName() + ";" + i.getValue().toString() + ";" + i.getParentMod().getName() + "\r\n");
                    }
                    out.write(i.getName() + ":" + i.getValue().toString() + ":" + i.getParentMod().getName() + "\r\n");
                }
                if (i.isEnum()) {
                    out.write(i.getName() + ":" + i.getValue().toString() + ":" + i.getParentMod().getName() + "\r\n");
                }

            }
            out.close();
        } catch (Exception e) {
        }
    }


    public void loadSettingsList() {

        //slider
        try {
            File file = new File(RuniteSettings.getAbsolutePath(), "Slider.txt");
            FileInputStream fstream = new FileInputStream(file.getAbsolutePath());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    String curLine = line.trim();
                    String name = curLine.split(":")[0];
                    String isOn = (curLine.split(":")[1]);
                    String m = curLine.split(":")[2];
                    Setting mod = Runite.getInstance().settingManager.getSettingByMod(ModuleManager.getModuleByName(m), name);
                    Number type = 0;
                    if (mod.getValue() instanceof Double) {
                        type = NumberUtil.createDouble(isOn);
                    } else if (mod.getValue() instanceof Integer) {
                        type = NumberUtil.createInteger(isOn);
                    } else if (mod.getValue() instanceof Float) {
                        type = NumberUtil.createFloat(isOn);
                    } else if (mod.getValue() instanceof Long) {
                        type = NumberUtil.createLong(isOn);
                    } else if (mod.getValue() instanceof Short) {
                        type = NumberUtil.createShort(isOn);
                    }
                    mod.setValue(type);
                } catch (Exception e) {
                    // empty
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
            //saveSettingsList();
        }
        //check
        try {
            File file = new File(RuniteSettings.getAbsolutePath(), "Check.txt");
            FileInputStream fstream = new FileInputStream(file.getAbsolutePath());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    String curLine = line.trim();
                    String name = curLine.split(":")[0];
                    String isOn = (curLine.split(":")[1]);
                    String m = curLine.split(":")[2];
                    Setting mod = Runite.getInstance().settingManager.getSettingByMod(Runite.getInstance().moduleManager.getModuleByName(m), name);
                    mod.setValue(Boolean.parseBoolean(isOn));
                } catch (Exception e) {
                    // empty
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
            //saveSettingsList();
        }

        //Combo
        try {
            File file = new File(RuniteSettings.getAbsolutePath(), "Combo.txt");
            FileInputStream fstream = new FileInputStream(file.getAbsolutePath());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    String curLine = line.trim();
                    String name;
                    String isOn;
                    String m;
                    if (curLine.contains(";")) {
                        name = curLine.split(";")[0];
                        isOn = (curLine.split(";")[1]);
                        m = curLine.split(";")[2];
                    } else {
                        name = curLine.split(":")[0];
                        isOn = (curLine.split(":")[1]);
                        m = curLine.split(":")[2];
                    }
                    Setting mod = Runite.getInstance().settingManager.getSettingByMod(Runite.getInstance().moduleManager.getModuleByName(m), name);
                    if (mod.isEnum()) {
                        mod.setEnumValue(isOn);
                    } else {
                        mod.setValue(isOn);
                    }
                } catch (Exception e) {
                    // empty
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
            //saveSettingsList();
        }
    }
}