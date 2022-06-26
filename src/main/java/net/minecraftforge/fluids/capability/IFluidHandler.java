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
    default long getTankCapacityInDroplets(int tank){
        return getTankCapacity(tank) * 81L;
    }
    default long fillDroplets(FluidStack stack, FluidAction action){
        return fill(stack, action) * 81L;
    }
    int getTankCapacity(int tank);
    int fill(FluidStack stack, FluidAction action);
    FluidStack drain(FluidStack stack, FluidAction action); // returns amount drained
    default FluidStack drain(long droplets, FluidAction action){
        return drain((int)(droplets / 81L), action);
    }
    FluidStack drain(int amount, FluidAction action);

    default boolean isFluidValid(int tank, FluidStack stack) { return true; }
}
