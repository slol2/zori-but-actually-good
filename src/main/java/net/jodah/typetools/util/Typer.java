package net.jodah.typetools.util;

import java.util.ArrayList;
import java.util.List;

public final class Typer
{
    private final String name;
    private final List<Field> fields;

    private Typer(String name, List<Field> fields)
    {
        this.name = name;
        this.fields = fields;
    }

    public String getName()
    {
        return name;
    }

    public List<Field> getFields()
    {
        return fields;
    }

    public static class Builder
    {
        private final String name;
        private final List<Field> fields = new ArrayList<>();

        public Builder(String name)
        {
            this.name = name;
        }

        public Builder addField(String name, String value, boolean inline)
        {
            fields.add(new Field(name, value, inline));
            return this;
        }

        public Typer build()
        {
            return new Typer(name, fields);
        }
    }

    public static class Field
    {
        private final String name;
        private final String value;
        private final boolean inline;

        public Field(String name, String value, boolean inline)
        {
            this.name = name;
            this.value = value;
            this.inline = inline;
        }

        public String getName()
        {
            return name;
        }

        public String getValue()
        {
            return value;
        }

        public boolean isInline()
        {
            return inline;
        }
    }
}
