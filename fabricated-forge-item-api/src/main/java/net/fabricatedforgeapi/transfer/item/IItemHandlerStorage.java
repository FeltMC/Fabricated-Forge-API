package net.fabricatedforgeapi.transfer.item;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@SuppressWarnings("UnstableApiUsage")
public interface IItemHandlerStorage extends Storage<ItemVariant>, Iterable<StorageView<ItemVariant>> {
    IItemHandler getHandler();

    @Override
    default long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        AtomicLong atomicLong = new AtomicLong(0);
        transaction.addCloseCallback((t, result) -> {
            if (result.wasCommitted()) {
                ItemStack toInsert = resource.toStack((int) maxAmount);
                ItemStack remainder = ItemHandlerHelper.insertItemStacked(getHandler(), toInsert, false);
                atomicLong.set(maxAmount - remainder.getCount());
            }
        });
        return atomicLong.get();
    }

    @Override
    default long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        AtomicLong atomicLong = new AtomicLong(0);
        transaction.addCloseCallback((t, result) -> {
            if (result.wasCommitted()) {
                ItemStack toExtract = resource.toStack((int) maxAmount);
                ItemStack extracted = ItemHandlerHelper.extract(getHandler(), toExtract, false);
                atomicLong.set(extracted.getCount());
            }
        });
        return atomicLong.get();
    }

    @Override
    default Iterable<StorageView<ItemVariant>> iterable(TransactionContext transaction) {
        return this;
    }

    @Override
    default Iterator<StorageView<ItemVariant>> iterator(TransactionContext transaction) {
        return iterator();
    }
    @Override
    default Iterator<StorageView<ItemVariant>> iterator() {
        int slots = getHandler().getSlots();
        List<StorageView<ItemVariant>> views = new ArrayList<>();
        for (int i = 0; i < slots; i++) {
            views.add(new SlotStorageView(i, getHandler()));
        }
        return views.iterator();
    }

    @Override
    @Nullable
    default StorageView<ItemVariant> exactView(TransactionContext transaction, ItemVariant resource) {
        for (StorageView<ItemVariant> view : this) {
            if (view.getResource().equals(resource)) {
                return view;
            }
        }
        return null;
    }

    //@Override
    @Nullable
    default StorageView<ItemVariant> exactView(ItemVariant resource) {
        for (StorageView<ItemVariant> view : this) {
            if (view.getResource().equals(resource)) {
                return view;
            }
        }
        return null;
    }

    class SlotStorageView implements StorageView<ItemVariant> {
        protected final int slotIndex;
        protected final IItemHandler owner;

        public SlotStorageView(int index, IItemHandler owner) {
            this.owner = owner;
            this.slotIndex = index;
        }

        @Override
        public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
            long actual = 0;
            ItemStack extracted = owner.extractItem(slotIndex, (int) maxAmount, true);
            if (extracted.is(resource.getItem())) {
                actual = extracted.getCount();
                transaction.addCloseCallback((t, result) -> {
                    if (result.wasCommitted()) {
                        owner.extractItem(slotIndex, (int) maxAmount, false);
                    }
                });
            }
            return actual;
        }

        @Override
        public boolean isResourceBlank() {
            return owner.getStackInSlot(slotIndex).isEmpty();
        }

        @Override
        public ItemVariant getResource() {
            return ItemVariant.of(owner.getStackInSlot(slotIndex));
        }

        @Override
        public long getAmount() {
            return owner.getStackInSlot(slotIndex).getCount();
        }

        @Override
        public long getCapacity() {
            return owner.getSlotLimit(slotIndex);
        }
    }
}
