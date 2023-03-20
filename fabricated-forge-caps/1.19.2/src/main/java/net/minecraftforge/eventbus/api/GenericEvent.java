package net.minecraftforge.eventbus.api;

import java.lang.reflect.Type;

public class GenericEvent<T> extends Event implements IGenericEvent<T>
{
    private Class<T> type;
    public GenericEvent() {}
    protected GenericEvent(Class<T> type)
    {
        this.type = type;
    }

    @Override
    public Type getGenericType()
    {
        return type;
    }
}
