package io.github.feltmc.fluidhandler_extras.mixin;

import io.github.feltmc.fluidhandler_extras.IFluidHandlerExtension;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = IFluidHandler.class, remap = false)
public interface IFluidHandlerMixin extends IFluidHandlerExtension {
    @Shadow
    int getTankCapacity(int tank);
    @Shadow
    int fill(FluidStack stack, IFluidHandler.FluidAction action);
    @Shadow
    FluidStack drain(int i, IFluidHandler.FluidAction fluidAction);
    @Override
    default long getTankCapacityInDroplets(int tank){
        return getTankCapacity(tank);
    }
    @Override
    default long fillDroplets(FluidStack stack, IFluidHandler.FluidAction action){
        return fill(stack, action);
    }

    @Override
    default FluidStack drain(long i, IFluidHandler.FluidAction fluidAction){
        return drain((int)i, fluidAction);
    }
}
