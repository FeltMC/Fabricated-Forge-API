/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.capabilities;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraftforge.fml.event.IModBusEvent;
import org.objectweb.asm.Type;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * This event fires when it is time to register your capabilities.
 * @see Capability
 */
public final class RegisterCapabilitiesEvent extends net.minecraftforge.eventbus.api.Event implements IModBusEvent
{
    public static final Event<Register> REGISTER_CAPS = EventFactory.createArrayBacked(Register.class, listeners -> event -> {
        for (Register listener : listeners) {
            listener.accept(event);
            if (event.isCanceled()){
                return;
            }
        }
    });

    @FunctionalInterface
    public interface Register extends Consumer<RegisterCapabilitiesEvent>{
    }

    /**
     * Registers a capability to be consumed by others.
     * APIs who define the capability should call this.
     * To retrieve the Capability instance, use the @CapabilityInject annotation.
     *
     * @param type The type to be registered
     */
    public <T> void register(Class<T> type)
    {
        Objects.requireNonNull(type,"Attempted to register a capability with invalid type");
        CapabilityManager.INSTANCE.get(Type.getInternalName(type), true);
    }
}