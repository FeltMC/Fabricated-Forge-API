package io.github.feltmc.fluidhandler_extras;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public interface IFluidHandlerExtension {
    long getTankCapacityInDroplets(int tank);

    long fillDroplets(FluidStack stack, IFluidHandler.FluidAction action);

    FluidStack drain(long i, IFluidHandler.FluidAction fluidAction);
}
