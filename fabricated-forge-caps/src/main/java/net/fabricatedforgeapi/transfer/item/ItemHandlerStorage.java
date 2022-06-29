package net.fabricatedforgeapi.transfer.item;

import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
