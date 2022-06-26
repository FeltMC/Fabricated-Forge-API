package net.fabricatedforgeapi.transfer.fluid.fluid;

import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.EmptyFluidHandler;

import java.util.Objects;

@SuppressWarnings({"UnstableApiUsage"})
public record FluidHandlerStorage(IFluidHandler handler) implements IFluidHandlerStorage {
    public FluidHandlerStorage(IFluidHandler handler) {
        this.handler = Objects.requireNonNullElse(handler, EmptyFluidHandler.INSTANCE);
    }

    @Override
    public IFluidHandler getHandler() {
        return handler;
    }
}
