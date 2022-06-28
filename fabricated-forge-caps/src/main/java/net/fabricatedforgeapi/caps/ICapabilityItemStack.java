package net.fabricatedforgeapi.caps;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public interface ICapabilityItemStack extends ICapabilitySerializable<CompoundTag> {
    default CapabilityProvider<ItemStack> getCapabilityProvider(){
        return null;
    }

    default void setCapNbt(CompoundTag tag){

    }

    default CompoundTag getCapNBT(){
        return null;
    }

    @Override
    default CompoundTag serializeNBT() {
        return null;
    }

    @Override
    default void deserializeNBT(CompoundTag nbt) {

    }
}
