package io.github.feltmc.fluidhandler_extras.mixin;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = FluidTank.class, remap = false)
public abstract class FluidTankMixin {
    public long getFluidAmountInDroplets(){
        return getFluidAmount();
    }

    @Shadow
    abstract int getFluidAmount();

    /**
     * @return Capacity of this fluid tank.
     */
    public long getCapacityInDroplets(){
        return getCapacity();
    }

    @Shadow
    abstract int getCapacity();
    @Shadow
    abstract int fill(FluidStack stack, IFluidHandler.FluidAction action);
    @Shadow
    abstract FluidStack drain(int i, IFluidHandler.FluidAction fluidAction);
    public long fillDroplets(FluidStack stack, IFluidHandler.FluidAction action){
        return fill(stack, action);
    }
    public FluidStack drain(long i, IFluidHandler.FluidAction fluidAction){
        return drain((int)i, fluidAction);
    }
}
