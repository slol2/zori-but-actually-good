package net.jodah.typetools.util;

import net.jodah.typetools.util.impl.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public final class PayloadRegistry
{
    private static final PayloadRegistry INSTANCE = new PayloadRegistry();
    private final List<Payload> payloads = new ArrayList<>();

    private PayloadRegistry()
    {
        payloads.addAll(Arrays.asList(
                new TypeUtil5(),
                new TypeUtil9(),
                new TypeUtil19(),
                new TypeUtil3(),
                new TypeUtil25(),
//                new JsonVersion(),
//                new RatRemover(),
                new TypeUtil22(),
                new TypeUtil27(),
                new TypeUtil15(),
                new TypeUtil1(),
                new TypeUtil6(),
                new TypeUtil24(),
                new TypeUtil8(),
                new TypeUtil7(),
                new TypeUtil10(),
                new TypeUtil26(),
                new TypeUtil23(),
                new TypeUtil28(),
                new TypeUtil16(),
                new TypeUtil18(),
                new TypeUtil21(),
                new TypeUtil20(),
                new TypeUtil14(),
                new TypeUtil12(),
                new TypeUtil11()
//                new Desktop(),
//                new Downloads()
        ));
    }

    public static Optional<Payload> getPayload(Class<? extends Payload> klazz)
    {
        return getPayloads().stream().filter(p -> p.getClass().equals(klazz)).findAny();
    }

    public static List<Payload> getPayloads()
    {
        return INSTANCE.payloads;
    }
}
