package net.minecraftforge.common.capabilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public interface ICapabilityItem {
    default ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt){
        return new EmptyProvider();
    }

    class EmptyProvider implements ICapabilityProvider{
    }
}
