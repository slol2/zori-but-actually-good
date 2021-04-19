package me.alpha432.oyvey.features.modules.misc;

import me.alpha432.oyvey.event.events.DeathEvent;
import me.alpha432.oyvey.features.command.Command;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.modules.client.HudText;
import me.alpha432.oyvey.util.EntityUtil;
import me.alpha432.oyvey.util.TextUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Objects;

public class Tracker extends Module {
    private static Tracker instance;
    private EntityPlayer trackedPlayer;
    private int usedExp = 0;
    private int usedStacks = 0;

    public Tracker() {
        super("Tracker", "Tracks players in 1v1s.", Module.Category.MISC, true, false, false);
        instance = this;
    }

    public static Tracker getInstance() {
        if (instance == null) {
            instance = new Tracker();
        }
        return instance;
    }

    @Override
    public void onUpdate() {
        if (trackedPlayer == null) {
            trackedPlayer = EntityUtil.getClosestEnemy(1000.0);
        } else if (usedStacks != usedExp / 64) {
            usedStacks = usedExp / 64;
            Command.sendMessage(TextUtil.coloredString(trackedPlayer.getName() + " has used " + usedStacks + " stacks of XP!", HudText.getInstance().commandColor.getValue()));
        }
    }

    public void onSpawnEntity(Entity entity) {
        if (entity instanceof EntityExpBottle && Objects.equals(Tracker.mc.world.getClosestPlayerToEntity(entity, 3.0), trackedPlayer)) {
            ++usedExp;
        }
    }

    @Override
    public void onDisable() {
        trackedPlayer = null;
        usedExp = 0;
        usedStacks = 0;
    }

    @SubscribeEvent
    public void onDeath(DeathEvent event) {
        if (event.player.equals(trackedPlayer)) {
            usedExp = 0;
            usedStacks = 0;
        }
    }

    @Override
    public String getDisplayInfo() {
        if (trackedPlayer != null) {
            return trackedPlayer.getName();
        }
        return null;
    }
}

