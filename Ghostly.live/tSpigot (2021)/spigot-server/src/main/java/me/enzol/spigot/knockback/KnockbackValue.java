package me.enzol.spigot.knockback;

public class KnockbackValue<T>
{
    public String id;
    public String name;
    public Class type;
    public T value;

    public KnockbackValue(final String id, final String name, final Class type, final T value) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.value = value;
    }
}
