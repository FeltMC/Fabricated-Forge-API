package io.github.feltmc.fluidhandler_extras.mixin;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nonnull;

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

    @Shadow @Nonnull FluidStack getFluid();

    @Shadow boolean isFluidValid(FluidStack stack);

    default long fillDroplets(FluidStack stack, IFluidHandler.FluidAction action){
        return fill(stack, action);
    }

    default FluidStack drain(long i, IFluidHandler.FluidAction fluidAction){
        return drain((int)i, fluidAction);
    }

    //IFluidHandler stuff

    @Override
    default int getTanks(){
        return 1;
    }

    @NotNull
    @Override
    default FluidStack getFluidInTank(int i){
        return getFluid();
    }

    @Override
    default int getTankCapacity(int i){
        return getCapacity();
    }

    @Override
    default boolean isFluidValid(int i, @NotNull FluidStack fluidStack){
        return isFluidValid(fluidStack);
    }
}
