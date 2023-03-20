package net.fabricatedforgeapi.caps;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.CapabilityProvider;

public interface ICapabilityBlockEntity {
    default CapabilityProvider<BlockEntity> getCapabilityProvider(){
        return null;
    }
}
