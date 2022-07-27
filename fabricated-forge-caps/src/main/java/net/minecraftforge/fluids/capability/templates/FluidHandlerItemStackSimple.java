/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fluids.capability.templates;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * FluidHandlerItemStackSimple is a template capability provider for ItemStacks.
 * Data is stored directly in the vanilla NBT, in the same way as the old ItemFluidContainer.
 *
 * This implementation only allows item containers to be fully filled or emptied, similar to vanilla buckets.
 */
public class FluidHandlerItemStackSimple implements IFluidHandlerItem, ICapabilityProvider
{
    public static final String FLUID_NBT_KEY = "Fluid";

    private final LazyOptional<IFluidHandlerItem> holder = LazyOptional.of(() -> this);

    @Nonnull
    protected ItemStack container;
    protected long capacity;

    /**
     * @param container  The container itemStack, data is stored on it directly as NBT.
     * @param capacity   The maximum capacity of this fluid tank.
     */
    public FluidHandlerItemStackSimple(@Nonnull ItemStack container, long capacity)
    {
        this.container = container;
        this.capacity = capacity;
    }

    public FluidHandlerItemStackSimple(@Nonnull ItemStack container, int capacity)
    {
        this.container = container;
        this.capacity = capacity * 81L;
    }

    @Nonnull
    @Override
    public ItemStack getContainer()
    {
        return container;
    }

    @Nonnull
    public FluidStack getFluid()
    {
        CompoundTag tagCompound = container.getTag();
        if (tagCompound == null || !tagCompound.contains(FLUID_NBT_KEY))
        {
            return FluidStack.EMPTY;
        }
        return FluidStack.loadFluidStackFromNBT(tagCompound.getCompound(FLUID_NBT_KEY));
    }

    protected void setFluid(FluidStack fluid)
    {
        if (!container.hasTag())
        {
            container.setTag(new CompoundTag());
        }

        CompoundTag fluidTag = new CompoundTag();
        fluid.writeToNBT(fluidTag);
        container.getTag().put(FLUID_NBT_KEY, fluidTag);
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

        return capacity;
    }

    @Override
    public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {

        return true;
    }

    @Override
    public long fillDroplets(@Nonnull FluidStack resource, FluidAction action)
    {
        if (container.getCount() != 1 || resource.isEmpty() || !canFillFluidType(resource))
        {
            return 0;
        }

        FluidStack contained = getFluid();
        if (contained.isEmpty())
        {
            long fillAmount = Math.min(capacity, resource.getRealAmount());
            if (fillAmount == capacity) {
                if (action.execute()) {
                    FluidStack filled = resource.copy();
                    filled.setAmount(fillAmount);
                    setFluid(filled);
                }

                return fillAmount;
            }
        }

        return 0;
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
        if (container.getCount() != 1 || resource.isEmpty() || !resource.isFluidEqual(getFluid()))
        {
            return FluidStack.EMPTY;
        }
        return drain(resource.getRealAmount(), action);
    }

    @Nonnull
    @Override
    public FluidStack drain(long maxDrain, FluidAction action)
    {
        if (container.getCount() != 1 || maxDrain <= 0)
        {
            return FluidStack.EMPTY;
        }

        FluidStack contained = getFluid();
        if (contained.isEmpty() || !canDrainFluidType(contained))
        {
            return FluidStack.EMPTY;
        }

        final long drainAmount = Math.min(contained.getRealAmount(), maxDrain);
        if (drainAmount == capacity) {
            FluidStack drained = contained.copy();

            if (action.execute()) {
                setContainerToEmpty();
            }

            return drained;
        }

        return FluidStack.EMPTY;
    }

    @Override
    public FluidStack drain(int amount, FluidAction action) {
        return drain(amount * 81L, action);
    }

    public boolean canFillFluidType(FluidStack fluid)
    {
        return true;
    }

    public boolean canDrainFluidType(FluidStack fluid)
    {
        return true;
    }

    /**
     * Override this method for special handling.
     * Can be used to swap out the container's item for a different one with "container.setItem".
     * Can be used to destroy the container with "container.stackSize--"
     */
    protected void setContainerToEmpty()
    {
        container.removeTagKey(FLUID_NBT_KEY);
    }

    @Override
    @Nonnull
    public <T> @NotNull LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing)
    {
        return CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY.orEmpty(capability, holder);
    }

    /**
     * Destroys the container item when it's emptied.
     */
    public static class Consumable extends FluidHandlerItemStackSimple
    {
        public Consumable(ItemStack container, long capacity)
        {
            super(container, capacity);
        }

        @Override
        protected void setContainerToEmpty()
        {
            super.setContainerToEmpty();
            container.shrink(1);
        }
    }

    /**
     * Swaps the container item for a different one when it's emptied.
     */
    public static class SwapEmpty extends FluidHandlerItemStackSimple
    {
        protected final ItemStack emptyContainer;

        public SwapEmpty(ItemStack container, ItemStack emptyContainer, long capacity)
        {
            super(container, capacity);
            this.emptyContainer = emptyContainer;
        }

        @Override
        protected void setContainerToEmpty()
        {
            super.setContainerToEmpty();
            container = emptyContainer;
        }
    }
}
