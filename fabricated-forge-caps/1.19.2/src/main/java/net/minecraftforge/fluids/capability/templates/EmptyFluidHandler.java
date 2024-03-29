/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fluids.capability.templates;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;

public class EmptyFluidHandler implements IFluidHandler
{
    public static final EmptyFluidHandler INSTANCE = new EmptyFluidHandler();

    protected EmptyFluidHandler() {}

    @Override
    public int getTanks() { return 1; }

    @Nonnull
    @Override
    public FluidStack getFluidInTank(int tank) { return FluidStack.EMPTY; }

    @Override
    public long getTankCapacityInDroplets(int tank) { return 0; }

    @Override
    public boolean isFluidValid(int tank, @Nonnull FluidStack stack) { return true; }

    @Override
    public long fillDroplets(FluidStack resource, FluidAction action)
    {
        return 0;
    }

    @Override
    public int getTankCapacity(int tank) {
        return 0;
    }

    @Override
    public int fill(FluidStack stack, FluidAction action) {
        return 0;
    }

    @Nonnull
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action)
    {
        return FluidStack.EMPTY;
    }

    @Nonnull
    @Override
    public FluidStack drain(long maxDrain, FluidAction action)
    {
        return FluidStack.EMPTY;
    }

    @Override
    public FluidStack drain(int amount, FluidAction action) {
        return FluidStack.EMPTY;
    }
}
