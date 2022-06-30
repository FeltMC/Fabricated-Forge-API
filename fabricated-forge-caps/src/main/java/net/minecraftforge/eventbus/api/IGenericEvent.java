package net.minecraftforge.eventbus.api;

import java.lang.reflect.Type;

public interface IGenericEvent<T>
{
    Type getGenericType();
}
