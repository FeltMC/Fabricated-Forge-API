package net.fabricatedforgeapi.fluid;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.EmptyFluidHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import static net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE;
import static net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.SIMULATE;

@SuppressWarnings({"UnstableApiUsage"})
public record FluidHandlerStorage(IFluidHandler handler) implements Storage<FluidVariant> {
    public FluidHandlerStorage(IFluidHandler handler) {
        this.handler = Objects.requireNonNullElse(handler, EmptyFluidHandler.INSTANCE);
    }

    @Override
    public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        long remainder = handler.fillLong(new FluidStack(resource, maxAmount), SIMULATE);
        transaction.addCloseCallback((t, result) -> {
            if (result.wasCommitted()) {
                handler.fillLong(new FluidStack(resource, maxAmount), EXECUTE);
            }
        });
        return remainder;
    }

    @Override
    public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        FluidStack extracted = handler.drain(new FluidStack(resource, maxAmount), SIMULATE);
        transaction.addCloseCallback((t, result) -> {
            if (result.wasCommitted()) {
                handler.drain(new FluidStack(resource, maxAmount), EXECUTE);
            }
        });
        return extracted.getAmount();
    }

    @Override
    public Iterator<StorageView<FluidVariant>> iterator(TransactionContext transaction) {
        int tanks = handler.getTanks();
        List<StorageView<FluidVariant>> views = new ArrayList<>();
        for (int i = 0; i < tanks; i++) {
            views.add(new TankStorageView(i, handler));
        }
        return views.iterator();
    }

    @Override
    public Iterable<StorageView<FluidVariant>> iterable(TransactionContext transaction) {
        int tanks = handler.getTanks();
        List<StorageView<FluidVariant>> views = new ArrayList<>();
        for (int i = 0; i < tanks; i++) {
            views.add(new TankStorageView(i, handler));
        }
        return views;
    }

    @Override
    @Nullable
    public StorageView<FluidVariant> exactView(TransactionContext transaction, FluidVariant resource) {
        for (StorageView<FluidVariant> view : iterable(transaction)) {
            if (view.getResource().equals(resource)) {
                return view;
            }
        }
        return null;
    }

    public static class TankStorageView implements StorageView<FluidVariant> {
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
            return drained.getAmount();
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
            return owner.getFluidInTank(tankIndex).getAmount();
        }

        @Override
        public long getCapacity() {
            return owner.getTankCapacityLong(tankIndex);
        }
    }
}
