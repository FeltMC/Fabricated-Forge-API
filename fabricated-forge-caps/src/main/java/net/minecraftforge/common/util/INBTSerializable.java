/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.util;

import net.minecraft.nbt.Tag;

/**
 * An interface designed to unify various things in the Minecraft
 * code base that can be serialized to and from a NBT tag.
 */
public interface INBTSerializable<T extends Tag> extends io.github.fabricators_of_create.porting_lib.util.INBTSerializable<T> {
    @Override
    T serializeNBT();

    @Override
    void deserializeNBT(T nbt);
}
