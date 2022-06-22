package net.fabricatedforgeapi.item;

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

@SuppressWarnings("UnstableApiUsage")
public interface IItemHandlerStorage extends Storage<ItemVariant> {
    IItemHandler getHandler();

    @Override
    default long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        ItemStack toInsert = resource.toStack((int) maxAmount);
        ItemStack remainder = ItemHandlerHelper.insertItemStacked(getHandler(), toInsert, true);
        transaction.addCloseCallback((t, result) -> {
            if (result.wasCommitted()) {
                ItemHandlerHelper.insertItemStacked(getHandler(), toInsert, false);
            }
        });
        return maxAmount - remainder.getCount();
    }

    @Override
    default long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        ItemStack toExtract = resource.toStack((int) maxAmount);
        ItemStack extracted = ItemHandlerHelper.extract(getHandler(), toExtract, true);
        transaction.addCloseCallback((t, result) -> {
            if (result.wasCommitted()) {
                ItemHandlerHelper.extract(getHandler(), toExtract, false);
            }
        });
        return extracted.getCount();
    }

    @Override
    default Iterable<StorageView<ItemVariant>> iterable(TransactionContext transaction) {
        int slots = getHandler().getSlots();
        List<StorageView<ItemVariant>> views = new ArrayList<>();
        for (int i = 0; i < slots; i++) {
            views.add(new IItemHandlerStorage.SlotStorageView(i, getHandler()));
        }
        return views;
    }

    @Override
    default Iterator<StorageView<ItemVariant>> iterator(TransactionContext transaction) {
        return iterable(transaction).iterator();
    }

    @Override
    @Nullable
    default StorageView<ItemVariant> exactView(TransactionContext transaction, ItemVariant resource) {
        for (StorageView<ItemVariant> view : iterable(transaction)) {
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
