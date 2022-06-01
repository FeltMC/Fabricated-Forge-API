package net.minecraftforge.common.capabilities;

import net.minecraft.core.Direction;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ICapabilityProvider {
    @Nonnull
    default <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction arg){
        return LazyOptional.empty();
    };

    @Nonnull
    default <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
        return this.getCapability(cap, null);
    }
}
