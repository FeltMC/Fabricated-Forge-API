/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.capabilities;

import io.github.fabricators_of_create.porting_lib.extensions.BlockEntityExtensions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;

public interface ICapabilityProviderImpl<B extends ICapabilityProviderImpl<B>> extends ICapabilityProvider
{
    default boolean areCapsCompatible(CapabilityProvider<B> other){
        return false;
    }
    default boolean areCapsCompatible(@Nullable CapabilityDispatcher other){
        return false;
    }

    default boolean areCapsCompatible(ItemStack other){
        return areCapsCompatible(other.getCapabilityProvider().getCapabilities());
    }
    default boolean areCapsCompatible(Entity other){
        return areCapsCompatible(other.getCapabilityProvider().getCapabilities());
    }
    default boolean areCapsCompatible(BlockEntity other){
        return areCapsCompatible(other.getCapabilityProvider().getCapabilities());
    }
    default void invalidateCaps(){
    }
    default void reviveCaps(){
    }

}
