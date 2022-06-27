package net.fabricatedforgeapi.caps.mixin;

import net.fabricatedforgeapi.caps.ICapabilityItemStack;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityDispatcher;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilityProviderImpl;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements ICapabilityProviderImpl.IItemStackCapProviderImpl, ICapabilityItemStack {
    @Shadow @Final @Deprecated private Item item;

    @Shadow public abstract void setTag(@Nullable CompoundTag compoundTag);

    @Shadow public abstract CompoundTag save(CompoundTag compoundTag);

    @Unique
    private CompoundTag capNBT;
    @Unique
    private final CapabilityProvider.AsField<ItemStack> capProvider = new CapabilityProvider.AsField<>(ItemStack.class, (ItemStack)(Object)this, true);

    @Inject(method = "<init>(Lnet/minecraft/world/level/ItemLike;I)V", at = @At("TAIL"))
    private void injectInit(ItemLike itemLike, int i, CallbackInfo ci){
        this.capNBT = null;
        forgeInit();
    }

    @Inject(method = "<init>(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("TAIL"))
    private void initInitCaps(CompoundTag compoundTag, CallbackInfo ci){
        capNBT = compoundTag.contains("ForgeCaps") ? compoundTag.getCompound("ForgeCaps") : null;
        forgeInit();
    }
    
    private void forgeInit(){
        capProvider.initInternal(() -> item.initCapabilities((ItemStack) (Object)this, this.capNBT));
        if (this.capNBT != null) capProvider.deserializeInternal(this.capNBT);
    }

    @Inject(method = "save", at = @At(value = "RETURN"))
    private void injectSerializeCaps(CompoundTag compound, CallbackInfoReturnable<CompoundTag> cir){
        CompoundTag caps = capProvider.serializeInternal();
        if (caps != null && !caps.isEmpty()) compound.put("ForgeCaps", caps);
    }

    @Inject(method = "copy", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;setPopTime(I)V", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD)
    private void injectCopy(CallbackInfoReturnable<ItemStack> cir, ItemStack itemStack){
        itemStack.setCapNbt(capProvider.serializeInternal());
    }

    public void setCapNBT(CompoundTag capNBT) {
        this.capNBT = capNBT;
        this.forgeInit();
    }

    @Override
    public CompoundTag getCapNBT() {
        return capNBT;
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return capProvider.getCapability(cap, side);
    }

    @Override
    public boolean areCapsCompatible(CapabilityProvider<ItemStack> other) {
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
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        save(tag);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        final ItemStack itemStack = ItemStack.of(nbt);
        this.setTag(itemStack.getTag());
        if (itemStack.getCapNBT() != null) capProvider.deserializeInternal(itemStack.getCapNBT());
    }
}
