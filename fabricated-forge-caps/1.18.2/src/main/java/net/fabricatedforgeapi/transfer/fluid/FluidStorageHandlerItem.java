package net.fabricatedforgeapi.transfer.fluid;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    public static class Provider implements ICapabilityProvider {

        private final LazyOptional<IFluidHandlerItem> fluid;

        public Provider(NonNullSupplier<IFluidHandlerItem> cap) {
            this.fluid = LazyOptional.of(cap);
        }
        @NotNull
        @Override
        public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            return CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY.orEmpty(cap, fluid);
        }
    }
}
