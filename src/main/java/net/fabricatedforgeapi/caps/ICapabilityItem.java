package net.fabricatedforgeapi.caps;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;

public interface ICapabilityItem {
    default ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt){
        return new EmptyProvider();
    }

    class EmptyProvider implements ICapabilityProvider{
    }
}
