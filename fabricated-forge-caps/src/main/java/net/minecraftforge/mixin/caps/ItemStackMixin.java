package net.minecraftforge.mixin.caps;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityItem;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemStackMixin implements ICapabilityProvider {
    @Unique
    private ICapabilityProvider capabilityProvider;
    @Unique
    private CompoundTag capNBT;

    @Inject(method = "<init>(Lnet/minecraft/world/level/ItemLike;)V", at = @At("TAIL"))
    private void injectCapInit(ItemLike itemLike, CallbackInfo ci){
        if (itemLike == null){
            return;
        }
        this.capabilityProvider = ((ICapabilityItem)itemLike.asItem()).initCapabilities(((ItemStack) (Object)this), this.capNBT);
        if (this.capNBT != null){

        }
    }

    @Inject(method = "save", at = @At("RETURN"))
    private void injectCapSave(CompoundTag compoundTag, CallbackInfoReturnable<CompoundTag> cir){
        /*CompoundTag cnbt = this.serializeCaps();
        if (cnbt != null && !cnbt.isEmpty()) {
            compoundTag.put("ForgeCaps", cnbt);
        }*/
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction arg) {
        return capabilityProvider.getCapability(capability, arg);
    }
}
