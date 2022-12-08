package net.fabricatedforgeapi.transfer.fluid;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE;
import static net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.SIMULATE;

@SuppressWarnings("UnstableApiUsage")
public interface IFluidHandlerStorage extends Storage<FluidVariant> {
    IFluidHandler getHandler();

    @Override
    default long insert(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        long fill = getHandler().fillDroplets(new FluidStack(resource, maxAmount), SIMULATE);
        transaction.addCloseCallback((t, result) -> {
            if (result.wasCommitted()) {
                getHandler().fillDroplets(new FluidStack(resource, maxAmount), EXECUTE);
            }
        });
        return fill;
    }

    @Override
    default long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        FluidStack toExtract = new FluidStack(resource, maxAmount);
        long extracted = getHandler().drain(toExtract, SIMULATE).getRealAmount();
        transaction.addCloseCallback((t, result) -> {
            if (result.wasCommitted()) {
                getHandler().drain(toExtract, EXECUTE);
            }
        });
        return extracted;
    }

    @Override
    default Iterator<StorageView<FluidVariant>> iterator(TransactionContext transaction) {
        int tanks = getHandler().getTanks();
        List<StorageView<FluidVariant>> views = new ArrayList<>();
        for (int i = 0; i < tanks; i++) {
            views.add(new TankStorageView(i, getHandler()));
        }
        return views.iterator();
    }

    @Override
    default Iterable<StorageView<FluidVariant>> iterable(TransactionContext transaction) {
        int tanks = getHandler().getTanks();
        List<StorageView<FluidVariant>> views = new ArrayList<>();
        for (int i = 0; i < tanks; i++) {
            views.add(new TankStorageView(i, getHandler()));
        }
        return views;
    }

    @Override
    @Nullable
    default StorageView<FluidVariant> exactView(TransactionContext transaction, FluidVariant resource) {
        for (StorageView<FluidVariant> view : iterable(transaction)) {
            if (view.getResource().equals(resource)) {
                return view;
            }
        }
        return null;
    }

    class TankStorageView implements StorageView<FluidVariant> {
        protected final int tankIndex;
        protected final IFluidHandler owner;

        public TankStorageView(int tankIndex, IFluidHandler owner) {
            this.tankIndex = tankIndex;
            this.owner = owner;
        }

        @Override
        public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
            FluidStack drained = owner.drain(new FluidStack(resource, maxAmount), SIMULATE);
            transaction.addCloseCallback((t, result) -> {
                if (result.wasCommitted()) {
                    owner.drain(new FluidStack(resource, maxAmount), EXECUTE);
                }
            });
            return drained.getRealAmount();
        }

        @Override
        public boolean isResourceBlank() {
            return owner.getFluidInTank(tankIndex).isEmpty();
        }

        @Override
        public FluidVariant getResource() {
            return owner.getFluidInTank(tankIndex).getType();
        }

        @Override
        public long getAmount() {
            return owner.getFluidInTank(tankIndex).getRealAmount();
        }

        @Override
        public long getCapacity() {
            return owner.getTankCapacityInDroplets(tankIndex);
        }
    }
}
