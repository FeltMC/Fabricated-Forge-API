package net.fabricatedforgeapi.mixin.caps;

import net.fabricatedforgeapi.caps.ICapabilityItem;
import net.fabricatedforgeapi.transfer.fluid.FluidStorageHandlerItem;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Item.class)
public class ItemMixin implements ICapabilityItem {
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        if (!stack.isEmpty()){
            ContainerItemContext ctx = ContainerItemContext.withInitial(stack);
            Storage<FluidVariant> storage = FluidStorage.ITEM.find(stack, ctx);
            if (storage != null){
                return new FluidStorageHandlerItem.Provider(() -> new FluidStorageHandlerItem(ctx, storage));
            }
        }
        return ICapabilityItem.super.initCapabilities(stack, nbt);
    }
}
