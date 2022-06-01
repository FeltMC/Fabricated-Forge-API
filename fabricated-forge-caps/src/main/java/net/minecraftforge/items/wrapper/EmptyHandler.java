package net.minecraftforge.items.wrapper;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;

public class EmptyHandler implements IItemHandlerModifiable {
    public static final IItemHandler INSTANCE = new EmptyHandler();

    public EmptyHandler() {
    }

    public int getSlots() {
        return 0;
    }

    @Nonnull
    public ItemStack getStackInSlot(int slot) {
        return ItemStack.EMPTY;
    }

    @Nonnull
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        return stack;
    }

    @Nonnull
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return ItemStack.EMPTY;
    }

    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
    }

    public int getSlotLimit(int slot) {
        return 0;
    }

    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return false;
    }
}
