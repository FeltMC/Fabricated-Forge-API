package io.github.feltmc.fluidhandler_extras.mixin;

import io.github.feltmc.fluidhandler_extras.FluidStackExtension;
import net.minecraftforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = FluidStack.class, remap = false)
public abstract class FluidStackMixin implements FluidStackExtension {
    @Shadow public abstract int getAmount();

    @Shadow public abstract void setAmount(int par1);

    @Override
    public long getRealAmount(){
        return getAmount();
    }

    @Override
    public void setAmount(long amount){
        setAmount((int)amount);
    }
}
