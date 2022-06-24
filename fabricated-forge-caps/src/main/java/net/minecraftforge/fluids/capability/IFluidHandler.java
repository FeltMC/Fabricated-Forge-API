package net.minecraftforge.fluids.capability;
import net.fabricatedforgeapi.fluid.IFluidHandlerStorage;
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
    long getTankCapacityLong(int tank);
    long fillLong(FluidStack stack, FluidAction action); // returns amount filled
    default int getTankCapacity(int tank){
        return (int) getTankCapacityLong(tank);
    }
    default int fill(FluidStack stack, FluidAction action){
        return (int) this.fillLong(stack, action);
    }
    FluidStack drain(FluidStack stack, FluidAction action); // returns amount drained
    FluidStack drain(long amount, FluidAction action); // returns amount drained
    default FluidStack drain(int amount, FluidAction action){
        return drain((long) amount, action);
    }

    default boolean isFluidValid(int tank, FluidStack stack) { return true; }
}
