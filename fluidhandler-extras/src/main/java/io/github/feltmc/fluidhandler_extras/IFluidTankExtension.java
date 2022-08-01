package io.github.feltmc.fluidhandler_extras;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public interface IFluidTankExtension {
    long getFluidAmountInDroplets();

    long getCapacityInDroplets();

    long fillDroplets(FluidStack stack, IFluidHandler.FluidAction action);

    FluidStack drain(long i, IFluidHandler.FluidAction fluidAction);
}
