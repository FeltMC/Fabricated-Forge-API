package net.fabricatedforgeapi.caps;

import net.fabricatedforgeapi.fluid.FluidHandlerStorage;
import net.fabricatedforgeapi.fluid.FluidStorageHandler;
import net.fabricatedforgeapi.fluid.FluidStorageHandlerItem;
import net.fabricatedforgeapi.item.ItemHandlerStorage;
import net.fabricatedforgeapi.item.ItemStorageHandler;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CapUtils {

    public static LazyOptional<IItemHandler> getWrappedItemHandler(BlockEntity be, @Nullable Direction side) {
        // client handling
        if (Objects.requireNonNull(be.getLevel()).isClientSide()) {
            return LazyOptional.empty();
        }
        // external handling
        List<Storage<ItemVariant>> itemStorages = new ArrayList<>();
        Level l = be.getLevel();
        BlockPos pos = be.getBlockPos();
        BlockState state = be.getBlockState();

        for (Direction direction : getDirections(side)) {
            Storage<ItemVariant> itemStorage = ItemStorage.SIDED.find(l, pos, state, be, direction);

            if (itemStorage != null) {
                if (itemStorages.size() == 0) {
                    itemStorages.add(itemStorage);
                    continue;
                }

                for (Storage<ItemVariant> storage : itemStorages) {
                    if (!Objects.equals(itemStorage, storage)) {
                        itemStorages.add(itemStorage);
                        break;
                    }
                }
            }
        }


        if (itemStorages.isEmpty()) return LazyOptional.empty();
        if (itemStorages.size() == 1) return simplifyItem(itemStorages.get(0));
        return simplifyItem(new CombinedStorage<>(itemStorages));
    }

    public static LazyOptional<IFluidHandler> getWrappedFluidHandler(BlockEntity be, @Nullable Direction side) {
        boolean client = Objects.requireNonNull(be.getLevel()).isClientSide();
        // client handling
        if (client) { // TODO this system might be unnecessary
//            IFluidHandler cached = FluidTileDataHandler.getCachedHandler(be);
//            return LazyOptional.ofObject(cached);
        }
        // external handling
        List<Storage<FluidVariant>> fluidStorages = new ArrayList<>();
        Level l = be.getLevel();
        BlockPos pos = be.getBlockPos();
        BlockState state = be.getBlockState();

        for (Direction direction : getDirections(side)) {
            Storage<FluidVariant> fluidStorage = FluidStorage.SIDED.find(l, pos, state, be, direction);

            if (fluidStorage != null) {
                if (fluidStorages.size() == 0) {
                    fluidStorages.add(fluidStorage);
                    continue;
                }

                for (Storage<FluidVariant> storage : fluidStorages) {
                    if (!Objects.equals(fluidStorage, storage)) {
                        fluidStorages.add(fluidStorage);
                        break;
                    }
                }
            }
        }

        if (fluidStorages.isEmpty()) return LazyOptional.empty();
        if (fluidStorages.size() == 1) return simplifyFluid(fluidStorages.get(0));
        return simplifyFluid(new CombinedStorage<>(fluidStorages));
    }

    // Fluid-containing items

    public static LazyOptional<IFluidHandlerItem> getFluidHandlerItem(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return LazyOptional.empty();
        ContainerItemContext ctx = ContainerItemContext.withInitial(stack);
        Storage<FluidVariant> fluidStorage = FluidStorage.ITEM.find(stack, ctx);
        return fluidStorage == null ? LazyOptional.empty() : LazyOptional.of(() -> new FluidStorageHandlerItem(ctx, fluidStorage));
    }
    public static LazyOptional<IItemHandler> simplifyItem(Storage<ItemVariant> storage) {
        if (storage == null) return LazyOptional.empty();
        if (storage instanceof ItemHandlerStorage handler) return LazyOptional.of(() -> handler.getHandler());
        return LazyOptional.of(() -> new ItemStorageHandler(storage));
    }

    public static LazyOptional<IFluidHandler> simplifyFluid(Storage<FluidVariant> storage) {
        if (storage == null) return LazyOptional.empty();
        if (storage instanceof FluidHandlerStorage handler) return LazyOptional.of(() -> handler.handler());
        return LazyOptional.of(() -> new FluidStorageHandler(storage));
    }

    private static Direction[] getDirections(@Nullable Direction direction) {
        if (direction == null) return Direction.values();
        return new Direction[] {direction};
    }
}
