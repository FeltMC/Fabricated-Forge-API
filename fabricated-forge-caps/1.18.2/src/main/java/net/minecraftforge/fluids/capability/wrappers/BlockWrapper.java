/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fluids.capability.wrappers;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.templates.VoidFluidHandler;

/**
 * Wrapper around any block, only accounts for fluid placement, otherwise the block acts a void.
 * If the block in question inherits from the Forge implementations,
 * consider using {@link FluidBlockWrapper}.
 */
public class BlockWrapper extends VoidFluidHandler
{
    protected final BlockState state;
    protected final Level world;
    protected final BlockPos blockPos;

    public BlockWrapper(BlockState state, Level world, BlockPos blockPos)
    {
        this.state = state;
        this.world = world;
        this.blockPos = blockPos;
    }

    @Override
    public long fillDroplets(FluidStack resource, FluidAction action) {
        // NOTE: "Filling" means placement in this context!
        if (resource.getRealAmount() < FluidConstants.BUCKET)
        {
            return 0;
        }
        if (action.execute())
        {
            FluidUtil.destroyBlockOnFluidPlacement(world, blockPos);
            world.setBlock(blockPos, state, Block.UPDATE_ALL_IMMEDIATE);
        }
        return FluidConstants.BUCKET;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action)
    {
        return (int) (fillDroplets(resource, action) / 81);
    }

    public static class LiquidContainerBlockWrapper extends VoidFluidHandler
    {
        protected final LiquidBlockContainer liquidContainer;
        protected final Level world;
        protected final BlockPos blockPos;

        public LiquidContainerBlockWrapper(LiquidBlockContainer liquidContainer, Level world, BlockPos blockPos)
        {
            this.liquidContainer = liquidContainer;
            this.world = world;
            this.blockPos = blockPos;
        }

        @Override
        public long fillDroplets(FluidStack resource, FluidAction action) {
            // NOTE: "Filling" means placement in this context!
            if (resource.getAmount() >= FluidConstants.BUCKET)
            {
                BlockState state = world.getBlockState(blockPos);
                if (liquidContainer.canPlaceLiquid(world, blockPos, state, resource.getFluid()))
                {
                    //If we are executing try to actually fill the container, if it failed return that we failed
                    if (action.simulate() || liquidContainer.placeLiquid(world, blockPos, state, resource.getFluid().getAttributes().getStateForPlacement(world, blockPos, resource.toPortingLibStack())))
                    {
                        return FluidConstants.BUCKET;
                    }
                }
            }
            return 0;
        }

        @Override
        public int fill(FluidStack resource, FluidAction action)
        {
            return (int) (fillDroplets(resource, action) / 81);
        }
    }
}
