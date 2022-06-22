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
public class ItemHandlerStorage implements IItemHandlerStorage {
    @Nonnull
    protected IItemHandler handler;

    public ItemHandlerStorage(@Nullable IItemHandler handler) {
        if (handler == null) {
            this.handler = EmptyHandler.INSTANCE;
        } else {
            this.handler = handler;
        }
    }

    @Override
    @Nonnull
    public IItemHandler getHandler() {
        return handler;
    }

}
