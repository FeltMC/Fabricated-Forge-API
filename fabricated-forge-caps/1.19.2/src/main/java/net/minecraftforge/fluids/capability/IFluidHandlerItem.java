package net.minecraftforge.fluids.capability;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface IFluidHandlerItem extends IFluidHandler{
    /**
     * Get the container currently acted on by this fluid handler.
     * The ItemStack may be different from its initial state, in the case of fluid containers that have different items
     * for their filled and empty states.
     * May be an empty item if the container was drained and is consumable.
     */
    @NotNull
    ItemStack getContainer();
}
