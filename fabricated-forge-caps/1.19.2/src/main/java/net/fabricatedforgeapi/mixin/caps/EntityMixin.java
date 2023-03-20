package net.fabricatedforgeapi.mixin.caps;

import net.fabricatedforgeapi.caps.ICapabilityEntity;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityDispatcher;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import net.minecraftforge.common.capabilities.IEntityCapProviderImpl;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin implements IEntityCapProviderImpl, ICapabilityEntity {
    @Shadow protected abstract void unsetRemoved();

    @Unique
    private CapabilityProvider.AsField<Entity> capProvider = new CapabilityProvider.AsField<>(Entity.class, (Entity)(Object)this);

    @Inject(method = "<init>", at = @At("TAIL"))
    private void injectInit(EntityType entityType, Level level, CallbackInfo ci){
        capProvider.initInternal();
    }

    @Inject(method = "saveWithoutId", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V", shift = At.Shift.BEFORE))
    private void injectSerializeCaps(CompoundTag compound, CallbackInfoReturnable<CompoundTag> cir){
        CompoundTag caps = capProvider.serializeInternal();
        if (caps != null) compound.put("ForgeCaps", caps);
    }

    @Inject(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V", shift = At.Shift.BEFORE))
    private void injectDeserializeCaps(CompoundTag compound, CallbackInfo ci){
        if (compound.contains("ForgeCaps", 10)) {
            capProvider.deserializeInternal(compound);
        }
    }

    @Inject(method = "remove", at = @At("TAIL"))
    private void injectInvalidateCaps(Entity.RemovalReason reason, CallbackInfo ci){
        this.invalidateCaps();
    }

    @Inject(method = "unsetRemoved", at = @At("TAIL"))
    private void injectReviveCaps(CallbackInfo ci){
        this.reviveCaps();
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return capProvider.getCapability(cap, side);
    }

    @Override
    public boolean areCapsCompatible(CapabilityProvider<Entity> other) {
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
    public CapabilityProvider<Entity> getCapabilityProvider() {
        return capProvider;
    }
}
