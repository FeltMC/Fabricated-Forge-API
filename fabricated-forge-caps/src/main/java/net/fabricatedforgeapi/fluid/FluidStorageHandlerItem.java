package net.fabricatedforgeapi.fluid;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

@SuppressWarnings({"UnstableApiUsage"})
public class FluidStorageHandlerItem extends FluidStorageHandler implements IFluidHandlerItem {
    protected ContainerItemContext ctx;

    public FluidStorageHandlerItem(ContainerItemContext ctx, Storage<FluidVariant> storage) {
        super(storage);
        this.ctx = ctx;
    }

    @Override
    public ItemStack getContainer() {
        ItemStack stack = ctx.getItemVariant().toStack();
        if (stack.isEmpty()) return stack;
        stack.setCount((int) ctx.getAmount());
        return stack;
    }
}
