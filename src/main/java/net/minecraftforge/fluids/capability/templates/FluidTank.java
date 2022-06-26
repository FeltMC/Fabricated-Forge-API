/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fluids.capability.templates;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

/**
 * Flexible implementation of a Fluid Storage object. NOT REQUIRED.
 *
 * @author King Lemming
 */
public class FluidTank implements IFluidHandler, IFluidTank {

    protected Predicate<FluidStack> validator;
    @Nonnull
    protected FluidStack fluid = FluidStack.EMPTY;
    protected long capacity;

    public FluidTank(long capacity)
    {
        this(capacity, e -> true);
    }

    public FluidTank(long capacity, Predicate<FluidStack> validator)
    {
        this.capacity = capacity;
        this.validator = validator;
    }

    public FluidTank setCapacity(long capacity)
    {
        this.capacity = capacity;
        return this;
    }

    public FluidTank setValidator(Predicate<FluidStack> validator)
    {
        if (validator != null) {
            this.validator = validator;
        }
        return this;
    }

    public boolean isFluidValid(FluidStack stack)
    {
        return validator.test(stack);
    }

    public long getCapacityInDroplets()
    {
        return capacity;
    }

    @Override
    public int getCapacity() {
        return (int) (getCapacityInDroplets()/81);
    }

    @Nonnull
    public FluidStack getFluid()
    {
        return fluid;
    }

    public long getFluidAmountInDroplets()
    {
        return fluid.getRealAmount();
    }

    @Override
    public int getFluidAmount() {
        return (int) (getFluidAmountInDroplets() / 81);
    }

    public FluidTank readFromNBT(CompoundTag nbt) {

        FluidStack fluid = FluidStack.loadFluidStackFromNBT(nbt);
        setFluid(fluid);
        return this;
    }

    public CompoundTag writeToNBT(CompoundTag nbt) {

        fluid.writeToNBT(nbt);

        return nbt;
    }

    @Override
    public int getTanks() {

        return 1;
    }

    @Nonnull
    @Override
    public FluidStack getFluidInTank(int tank) {

        return getFluid();
    }

    @Override
    public long getTankCapacityInDroplets(int tank) {
        return getCapacityInDroplets();
    }

    @Override
    public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {

        return isFluidValid(stack);
    }

    @Override
    public long fillDroplets(FluidStack resource, FluidAction action)
    {
        if (resource.isEmpty() || !isFluidValid(resource))
        {
            return 0;
        }
        if (action.simulate())
        {
            if (fluid.isEmpty())
            {
                return Math.min(capacity, resource.getAmount());
            }
            if (!fluid.isFluidEqual(resource))
            {
                return 0;
            }
            return Math.min(capacity - fluid.getAmount(), resource.getAmount());
        }
        if (fluid.isEmpty())
        {
            fluid = new FluidStack(resource, Math.min(capacity, resource.getAmount()));
            onContentsChanged();
            return fluid.getAmount();
        }
        if (!fluid.isFluidEqual(resource))
        {
            return 0;
        }
        long filled = capacity - fluid.getAmount();

        if (resource.getAmount() < filled)
        {
            fluid.grow(resource.getAmount());
            filled = resource.getAmount();
        }
        else
        {
            fluid.setAmount(capacity);
        }
        if (filled > 0)
            onContentsChanged();
        return filled;
    }

    @Override
    public int getTankCapacity(int tank) {
        return (int) (getTankCapacityInDroplets(tank) / 81);
    }

    @Override
    public int fill(FluidStack stack, FluidAction action) {
        return (int) (fillDroplets(stack, action) / 81);
    }

    @Nonnull
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action)
    {
        if (resource.isEmpty() || !resource.isFluidEqual(fluid))
        {
            return FluidStack.EMPTY;
        }
        return drain(resource.getAmount(), action);
    }

    @Nonnull
    @Override
    public FluidStack drain(long maxDrain, FluidAction action)
    {
        long drained = maxDrain;
        if (fluid.getAmount() < drained)
        {
            drained = fluid.getAmount();
        }
        FluidStack stack = new FluidStack(fluid, drained);
        if (action.execute() && drained > 0)
        {
            fluid.shrink(drained);
            onContentsChanged();
        }
        return stack;
    }

    @Override
    public FluidStack drain(int amount, FluidAction action) {
        return drain(amount * 81L, action);
    }

    protected void onContentsChanged()
    {

    }

    public void setFluid(FluidStack stack)
    {
        this.fluid = stack;
    }

    public boolean isEmpty()
    {
        return fluid.isEmpty();
    }

    public long getSpace()
    {
        return Math.max(0, capacity - fluid.getAmount());
    }

}
