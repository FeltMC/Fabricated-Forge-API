package net.fabricatedforgeapi.caps;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.CapabilityProvider;

public interface ICapabilityEntity {
    default CapabilityProvider<Entity> getCapabilityProvider(){
        return null;
    }
}
