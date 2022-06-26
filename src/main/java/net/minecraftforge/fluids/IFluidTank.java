/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fluids;

import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;

/**
 * This interface represents a Fluid Tank. IT IS NOT REQUIRED but is provided for convenience.
 * You are free to handle Fluids in any way that you wish - this is simply an easy default way.
 * DO NOT ASSUME that these objects are used internally in all cases.
 */
public interface IFluidTank extends IFluidHandler {

    /**
     * @return FluidStack representing the fluid in the tank, null if the tank is empty.
     */
    @Nonnull
    FluidStack getFluid();

    /**
     * @return Current amount of fluid in the tank.
     */
    default long getFluidAmountInDroplets(){
        return getFluidAmount() * 81L;
    }

    int getFluidAmount();

    /**
     * @return Capacity of this fluid tank.
     */
    default long getCapacityInDroplets(){
        return getCapacity() * 81L;
    }

    int getCapacity();

    /**
     * @param stack Fluidstack holding the Fluid to be queried.
     * @return If the tank can hold the fluid (EVER, not at the time of query).
     */
    boolean isFluidValid(FluidStack stack);

}
