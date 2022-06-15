package net.minecraftforge.mixin.blockentity;

import net.fabricatedforgeapi.caps.CapUtils;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockEntity.class)
public class BlockEntityMixin implements ICapabilityProvider {

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) return CapUtils.getWrappedFluidHandler((BlockEntity) (Object)this, side).cast();
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return CapUtils.getWrappedItemHandler((BlockEntity) (Object)this, side).cast();
        return ICapabilityProvider.super.getCapability(cap, side);
    }
}
