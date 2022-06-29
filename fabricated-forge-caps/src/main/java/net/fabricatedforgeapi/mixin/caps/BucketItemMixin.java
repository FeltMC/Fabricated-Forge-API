package net.fabricatedforgeapi.mixin.caps;

import net.fabricatedforgeapi.transfer.fluid.BucketExtension;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BucketItem.class)
public class BucketItemMixin implements BucketExtension {

    @Shadow @Final private Fluid content;

    @Override
    public Fluid getFluid() {
        return this.content;
    }
}
