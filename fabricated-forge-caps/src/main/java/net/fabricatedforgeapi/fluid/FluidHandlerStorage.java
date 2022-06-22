package net.fabricatedforgeapi.fluid;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.EmptyFluidHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import static net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE;
import static net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.SIMULATE;

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
