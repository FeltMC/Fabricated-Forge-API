package net.fabricatedforgeapi.mixin.caps;

import net.fabricatedforgeapi.caps.ICapabilityBlockEntity;
import net.fabricatedforgeapi.transfer.TransferUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityDispatcher;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import net.minecraftforge.common.capabilities.IBlockEntityCapProviderImpl;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntity.class)
public class BlockEntityMixin implements IBlockEntityCapProviderImpl, ICapabilityBlockEntity {
    @Unique
    private final CapabilityProvider.AsField<BlockEntity> capProvider = new CapabilityProvider.AsField<>(BlockEntity.class, (BlockEntity)(Object)this);

    @Inject(method = "<init>", at = @At("TAIL"))
    private void injectInit(BlockEntityType blockEntityType, BlockPos blockPos, BlockState blockState, CallbackInfo ci){
        capProvider.initInternal();
    }

    @Inject(method = "saveAdditional", at = @At("TAIL"))
    private void injectSerializeCaps(CompoundTag tag, CallbackInfo ci){
        if (this.capProvider.getCapabilitiesInternal() != null){
            CompoundTag caps = capProvider.serializeInternal();
            if (caps != null) tag.put("ForgeCaps", caps);
        }

    }

    @Inject(method = "load", at = @At(value = "TAIL"))
    private void injectDeserializeCaps(CompoundTag compound, CallbackInfo ci){
        if (capProvider.getCapabilitiesInternal() != null && compound.contains("ForgeCaps")) {
            capProvider.deserializeInternal(compound);
        }
    }

    public void onChunkUnload(){
        this.invalidateCaps();
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return capProvider.getCapability(cap, side);
    }

    @Override
    public boolean areCapsCompatible(CapabilityProvider<BlockEntity> other) {
        return capProvider.areCapsCompatible(other);
    }

    @Override
    public boolean areCapsCompatible(@Nullable CapabilityDispatcher other) {
        return capProvider.areCapsCompatible(other);
    }

    @Override
    public void invalidateCaps() {
        capProvider.invalidateCaps();
    }

    @Override
    public void reviveCaps() {
        capProvider.reviveCaps();
    }

    @Override
    public CapabilityProvider<BlockEntity> getCapabilityProvider() {
        return capProvider;
    }
}
