package net.fabricatedforgeapi.caps.mixin;

import net.fabricatedforgeapi.caps.ICapabilityBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityDispatcher;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilityProviderImpl;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntity.class)
public class BlockEntityMixin implements ICapabilityProviderImpl.IBlockEntityCapProviderImpl, ICapabilityBlockEntity {
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

    @Inject(method = "setRemoved", at = @At("TAIL"))
    private void injectInvalidateCaps(CallbackInfo ci){
        this.invalidateCaps();
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
