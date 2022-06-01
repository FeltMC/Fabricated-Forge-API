package net.minecraftforge.items.wrapper;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class PlayerMainInvWrapper extends RangedWrapper {
    private final Inventory inventoryPlayer;

    public PlayerMainInvWrapper(Inventory inv) {
        super(new InvWrapper(inv), 0, inv.items.size());
        this.inventoryPlayer = inv;
    }

    @Nonnull
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        ItemStack rest = super.insertItem(slot, stack, simulate);
        if (rest.getCount() != stack.getCount()) {
            ItemStack inSlot = this.getStackInSlot(slot);
            if (!inSlot.isEmpty()) {
                if (this.getInventoryPlayer().player.level.isClientSide) {
                    inSlot.setPopTime(5);
                } else if (this.getInventoryPlayer().player instanceof ServerPlayer) {
                    this.getInventoryPlayer().player.containerMenu.broadcastChanges();
                }
            }
        }

        return rest;
    }

    public Inventory getInventoryPlayer() {
        return this.inventoryPlayer;
    }
}
