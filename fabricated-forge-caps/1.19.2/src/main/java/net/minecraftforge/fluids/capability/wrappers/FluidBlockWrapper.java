/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fluids.capability.wrappers;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;

//TODO
public class FluidBlockWrapper implements IFluidHandler
{
    protected final IFluidBlock fluidBlock;
    protected final Level world;
    protected final BlockPos blockPos;

    public FluidBlockWrapper(IFluidBlock fluidBlock, Level world, BlockPos blockPos)
    {
        this.fluidBlock = fluidBlock;
        this.world = world;
        this.blockPos = blockPos;
    }

    @Override
    public int getTanks()
    {
        return 1;
    }

    @Nonnull
    @Override
    public FluidStack getFluidInTank(int tank)
    {
        return tank == 0 ? fluidBlock.drain(world, blockPos, FluidAction.SIMULATE) : FluidStack.EMPTY;
    }

    @Override
    public long getTankCapacityInDroplets(int tank) {
        FluidStack stored = getFluidInTank(tank);
        if (!stored.isEmpty())
        {
            float filledPercentage = fluidBlock.getFilledPercentage(world, blockPos);
            if (filledPercentage > 0)
            {
                return (long) (stored.getRealAmount() / filledPercentage);
            }
        }
        return FluidConstants.BUCKET;
    }

    @Override
    public long fillDroplets(FluidStack stack, FluidAction action) {
        return 0;
    }

    @Override
    public int getTankCapacity(int tank)
    {
        return (int) (getTankCapacityInDroplets(tank) / 81);
    }

    @Override
    public boolean isFluidValid(int tank, @Nonnull FluidStack stack)
    {
        return stack.getFluid() == fluidBlock.getFluid();
    }

    @Override
    public int fill(FluidStack resource, FluidAction action)
    {
        return fluidBlock.place(world, blockPos, resource, action);
    }

    @Nonnull
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action)
    {
        if (!resource.isEmpty() && fluidBlock.canDrain(world, blockPos) && resource.getFluid() == fluidBlock.getFluid())
        {
            FluidStack simulatedDrained = fluidBlock.drain(world, blockPos, FluidAction.SIMULATE);
            if (simulatedDrained.getAmount() <= resource.getAmount() && resource.isFluidEqual(simulatedDrained))
            {
                if (action.execute())
                {
                    return fluidBlock.drain(world, blockPos, FluidAction.EXECUTE).copy();
                }
                return simulatedDrained.copy();
            }
        }
        return FluidStack.EMPTY;
    }

    @Override
    public FluidStack drain(long droplets, FluidAction action) {
        return null;
    }

    @Nonnull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action)
    {
        if (maxDrain > 0 && fluidBlock.canDrain(world, blockPos))
        {
            FluidStack simulatedDrained = fluidBlock.drain(world, blockPos, FluidAction.SIMULATE);
            if (simulatedDrained.getAmount() <= maxDrain)
            {
                if (action.execute())
                {
                    return fluidBlock.drain(world, blockPos, FluidAction.EXECUTE).copy();
                }
                return simulatedDrained.copy();
            }
        }
        return FluidStack.EMPTY;
    }
}
