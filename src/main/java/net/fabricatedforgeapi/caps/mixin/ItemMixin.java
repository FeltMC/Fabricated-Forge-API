package net.fabricatedforgeapi.caps.mixin;

import net.fabricatedforgeapi.caps.ICapabilityItem;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Item.class)
public class ItemMixin implements ICapabilityItem {

}
