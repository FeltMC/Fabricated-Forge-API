//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.items.wrapper;

import com.google.common.base.Preconditions;
import javax.annotation.Nonnull;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

public class RangedWrapper implements IItemHandlerModifiable {
    private final IItemHandlerModifiable compose;
    private final int minSlot;
    private final int maxSlot;

    public RangedWrapper(IItemHandlerModifiable compose, int minSlot, int maxSlotExclusive) {
        Preconditions.checkArgument(maxSlotExclusive > minSlot, "Max slot must be greater than min slot");
        this.compose = compose;
        this.minSlot = minSlot;
        this.maxSlot = maxSlotExclusive;
    }

    public int getSlots() {
        return this.maxSlot - this.minSlot;
    }

    @Nonnull
    public ItemStack getStackInSlot(int slot) {
        return this.checkSlot(slot) ? this.compose.getStackInSlot(slot + this.minSlot) : ItemStack.EMPTY;
    }

    @Nonnull
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        return this.checkSlot(slot) ? this.compose.insertItem(slot + this.minSlot, stack, simulate) : stack;
    }

    @Nonnull
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return this.checkSlot(slot) ? this.compose.extractItem(slot + this.minSlot, amount, simulate) : ItemStack.EMPTY;
    }

    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
        if (this.checkSlot(slot)) {
            this.compose.setStackInSlot(slot + this.minSlot, stack);
        }

    }

    public int getSlotLimit(int slot) {
        return this.checkSlot(slot) ? this.compose.getSlotLimit(slot + this.minSlot) : 0;
    }

    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return this.checkSlot(slot) ? this.compose.isItemValid(slot + this.minSlot, stack) : false;
    }

    private boolean checkSlot(int localSlot) {
        return localSlot + this.minSlot < this.maxSlot;
    }
}
