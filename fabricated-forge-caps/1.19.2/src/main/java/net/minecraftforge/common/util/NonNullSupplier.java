/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.util;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

/**
 * Equivalent to {@link Supplier}, except with nonnull contract.
 * 
 * @see Supplier
 */
@FunctionalInterface
public interface NonNullSupplier<T> extends io.github.fabricators_of_create.porting_lib.util.NonNullSupplier<T>
{
    @Override
    @NotNull
    T get();
}
