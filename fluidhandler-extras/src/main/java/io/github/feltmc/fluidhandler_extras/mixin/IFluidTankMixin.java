package io.github.feltmc.fluidhandler_extras.mixin;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = IFluidTank.class, remap = false)
public interface IFluidTankMixin extends IFluidHandler{
    default long getFluidAmountInDroplets(){
        return getFluidAmount();
    }

    @Shadow
    int getFluidAmount();

    /**
     * @return Capacity of this fluid tank.
     */
    default long getCapacityInDroplets(){
        return getCapacity();
    }

    @Shadow
    int getCapacity();
    @Shadow
    int fill(FluidStack stack, IFluidHandler.FluidAction action);
    @Shadow
    FluidStack drain(int i, IFluidHandler.FluidAction fluidAction);
    default long fillDroplets(FluidStack stack, IFluidHandler.FluidAction action){
        return fill(stack, action);
    }

    default FluidStack drain(long i, IFluidHandler.FluidAction fluidAction){
        return drain((int)i, fluidAction);
    }
}
