package net.minecraftforge.mixin.caps;

import net.fabricatedforgeapi.caps.CapUtils;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BlockEntity.class)
public class BlockEntityMixin implements ICapabilityProvider {
    @Unique
    LazyOptional<IFluidHandler> fluidHandler = null;
    @Unique
    LazyOptional<IItemHandler> itemHandler = null;

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            if (fluidHandler == null) fluidHandler = CapUtils.getWrappedFluidHandler((BlockEntity) (Object)this, side);
            return fluidHandler.cast();
        }
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (itemHandler == null) itemHandler = CapUtils.getWrappedItemHandler((BlockEntity) (Object)this, side);
            return itemHandler.cast();
        }
        return ICapabilityProvider.super.getCapability(cap, side);
    }
}
