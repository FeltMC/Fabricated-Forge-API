package net.fabricatedforgeapi.transfer.fluid;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"UnstableApiUsage"})
public class FluidStorageHandler implements IFluidHandler {
    protected final Storage<FluidVariant> storage;
    protected long version;
    protected int tanks;
    protected FluidStack[] stacks;
    protected Long[] capacities;

    public FluidStorageHandler(Storage<FluidVariant> storage) {
        this.storage = storage;
        this.version = storage.getVersion();
        updateContents();
    }

    public boolean shouldUpdate() {
        return storage.getVersion() != version;
    }

    private void updateContents() {
        List<FluidStack> stacks = new ArrayList<>();
        List<Long> capacities = new ArrayList<>();
        try (Transaction t = Transaction.openOuter()) {
            for (StorageView<FluidVariant> view : storage) {
                stacks.add(new FluidStack(view.getResource(), view.getAmount()));
                capacities.add(view.getCapacity());
            }
            t.abort();
        }
        this.stacks = stacks.toArray(FluidStack[]::new);
        this.capacities = capacities.toArray(Long[]::new);
        this.tanks = stacks.size();
        this.version = storage.getVersion();
    }

    private boolean validIndex(int tank) {
        return tank >= 0 && tank < tanks;
    }

    /* IFluidHandler */
    @Override
    public int getTanks() {
        if (shouldUpdate())
            updateContents();
        return tanks;
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        if (validIndex(tank)) {
            if (shouldUpdate())
                updateContents();
            return stacks[tank].copy();
        }
        return FluidStack.EMPTY;
    }

    @Override
    public long getTankCapacityInDroplets(int tank) {
        if (validIndex(tank)) {
            if (shouldUpdate())
                updateContents();
            return (int) (long) capacities[tank];
        }
        return 0;
    }

    @Override
    public long fillDroplets(FluidStack stack, FluidAction action) {
        if (stack.isEmpty())
            return 0;
        if (!storage.supportsInsertion())
            return 0;

        try (Transaction t = Transaction.openOuter()) {
            long filled = storage.insert(stack.getType(), stack.getRealAmount(), t);
            if (action.execute()) {
                t.commit();
                if (shouldUpdate())
                    updateContents();
            }
            return filled;
        }
    }

    @Override
    public int getTankCapacity(int tank) {
        return (int) (getTankCapacityInDroplets(tank) / 81);
    }

    @Override
    public int fill(FluidStack stack, FluidAction action) {
        return (int) (fillDroplets(stack, action) / 81);
    }

    @Override
    public FluidStack drain(FluidStack stack, FluidAction action) {
        if (stack.isEmpty())
            return FluidStack.EMPTY;
        if (!storage.supportsExtraction())
            return FluidStack.EMPTY;

        try (Transaction t = Transaction.openOuter()) {
            long extracted = storage.extract(stack.getType(), stack.getRealAmount(), t);
            if (action.execute()) {
                t.commit();
                if (shouldUpdate())
                    updateContents();
            }
            FluidStack copy = stack.copy();
            copy.setAmount(extracted);
            return copy;
        }
    }

    @Override
    public FluidStack drain(long toExtract, FluidAction action) {
        if (toExtract == 0)
            return FluidStack.EMPTY;
        if (!storage.supportsExtraction())
            return FluidStack.EMPTY;

        FluidStack extracted = FluidStack.EMPTY;
        try (Transaction t = Transaction.openOuter()) {
            for (StorageView<FluidVariant> view : storage) {
                FluidVariant var = view.getResource();
                if (var.isBlank() || !extracted.canFill(var)) continue;
                long drained = view.extract(var, toExtract, t);
                toExtract -= drained;
                if (drained != 0) {
                    if (extracted.isEmpty()) {
                        extracted = new FluidStack(var, drained);
                    } else if (extracted.canFill(var)) {
                        extracted.grow(drained);
                    }
                }
                if (toExtract <= 0) break;
            }
            if (action.execute()) {
                t.commit();
                if (shouldUpdate())
                    updateContents();
            }
        }
        return extracted;
    }

    @Override
    public FluidStack drain(int amount, FluidAction action) {
        return drain(amount * 81L, action);
    }
}
