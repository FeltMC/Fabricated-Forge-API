package net.fabricatedforgeapi.item;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.EmptyHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class ItemHandlerStorage implements Storage<ItemVariant> {
    @Nonnull
    protected IItemHandler handler;

    public ItemHandlerStorage(@Nullable IItemHandler handler) {
        if (handler == null) {
            this.handler = EmptyHandler.INSTANCE;
        } else {
            this.handler = handler;
        }
    }

    @Nonnull
    public IItemHandler getHandler() {
        return handler;
    }

    @Override
    public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        ItemStack toInsert = resource.toStack((int) maxAmount);
        ItemStack remainder = ItemHandlerHelper.insertItemStacked(handler, toInsert, true);
        transaction.addCloseCallback((t, result) -> {
            if (result.wasCommitted()) {
                ItemHandlerHelper.insertItemStacked(handler, toInsert, false);
            }
        });
        return maxAmount - remainder.getCount();
    }

    @Override
    public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        ItemStack toExtract = resource.toStack((int) maxAmount);
        ItemStack extracted = ItemHandlerHelper.extract(handler, toExtract, true);
        transaction.addCloseCallback((t, result) -> {
            if (result.wasCommitted()) {
                ItemHandlerHelper.extract(handler, toExtract, false);
            }
        });
        return extracted.getCount();
    }

    @Override
    public Iterable<StorageView<ItemVariant>> iterable(TransactionContext transaction) {
        int slots = handler.getSlots();
        List<StorageView<ItemVariant>> views = new ArrayList<>();
        for (int i = 0; i < slots; i++) {
            views.add(new SlotStorageView(i, handler));
        }
        return views;
    }

    @Override
    public Iterator<StorageView<ItemVariant>> iterator(TransactionContext transaction) {
        return iterable(transaction).iterator();
    }

    @Override
    @Nullable
    public StorageView<ItemVariant> exactView(TransactionContext transaction, ItemVariant resource) {
        for (StorageView<ItemVariant> view : iterable(transaction)) {
            if (view.getResource().equals(resource)) {
                return view;
            }
        }
        return null;
    }

    public static class SlotStorageView implements StorageView<ItemVariant> {
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
