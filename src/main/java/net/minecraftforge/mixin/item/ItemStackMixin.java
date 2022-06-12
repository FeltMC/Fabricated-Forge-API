package net.minecraftforge.mixin.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.extensions.IForgeItemStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemStack.class)
public class ItemStackMixin implements ICapabilityProvider, IForgeItemStack {
    @Override
    public void deserializeNBT(CompoundTag nbt) {

    }
}
