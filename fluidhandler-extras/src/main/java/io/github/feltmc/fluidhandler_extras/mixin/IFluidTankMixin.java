package io.github.feltmc.fluidhandler_extras.mixin;

import io.github.feltmc.fluidhandler_extras.IFluidTankExtension;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = IFluidTank.class, remap = false)
public interface IFluidTankMixin extends IFluidTankExtension {
    @Override
    default long getFluidAmountInDroplets(){
        return getFluidAmount();
    }

    @Shadow
    int getFluidAmount();

    /**
     * @return Capacity of this fluid tank.
     */
    @Override
    default long getCapacityInDroplets(){
        return getCapacity();
    }

    @Shadow
    int getCapacity();
    @Shadow
    int fill(FluidStack stack, IFluidHandler.FluidAction action);
    @Shadow
    FluidStack drain(int i, IFluidHandler.FluidAction fluidAction);
    @Override
    default long fillDroplets(FluidStack stack, IFluidHandler.FluidAction action){
        return fill(stack, action);
    }

    @Override
    default FluidStack drain(long i, IFluidHandler.FluidAction fluidAction){
        return drain((int)i, fluidAction);
    }
}
