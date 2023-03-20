package net.minecraftforge.common.capabilities;

import io.github.fabricators_of_create.porting_lib.extensions.BlockEntityExtensions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public interface IBlockEntityCapProviderImpl extends ICapabilityProviderImpl<BlockEntity>, BlockEntityExtensions {
    @Override
    default void invalidateCaps() {
        ICapabilityProviderImpl.super.invalidateCaps();
    }
}
