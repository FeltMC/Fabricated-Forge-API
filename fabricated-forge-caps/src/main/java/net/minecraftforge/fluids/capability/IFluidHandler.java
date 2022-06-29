package net.minecraftforge.fluids.capability;
import net.fabricatedforgeapi.transfer.fluid.IFluidHandlerStorage;
import net.minecraftforge.fluids.FluidStack;

public interface IFluidHandler extends IFluidHandlerStorage {

    @Override
    default IFluidHandler getHandler(){
        return this;
    }

    enum FluidAction {
        EXECUTE, SIMULATE;

        public boolean execute() {
            return this == EXECUTE;
        }

        public boolean simulate() {
            return this == SIMULATE;
        }
    }
    int getTanks();
    FluidStack getFluidInTank(int tank);
    long getTankCapacityInDroplets(int tank);
    long fillDroplets(FluidStack stack, FluidAction action);
    default int getTankCapacity(int tank){
        return (int) (getTankCapacityInDroplets(tank) / 81);
    }
    default int fill(FluidStack stack, FluidAction action){
        return (int) (fillDroplets(stack, action) / 81);
    }
    FluidStack drain(FluidStack stack, FluidAction action); // returns amount drained
    FluidStack drain(long droplets, FluidAction action);
    default FluidStack drain(int amount, FluidAction action){
        return drain(amount * 81L, action);
    }

    default boolean isFluidValid(int tank, FluidStack stack) { return true; }
}
