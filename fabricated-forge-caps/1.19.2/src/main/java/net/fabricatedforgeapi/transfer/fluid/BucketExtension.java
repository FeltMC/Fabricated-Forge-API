package net.fabricatedforgeapi.transfer.fluid;

import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

public interface BucketExtension {
    default Fluid getFluid(){
        return Fluids.EMPTY;
    }
}
