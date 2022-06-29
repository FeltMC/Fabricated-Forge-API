/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.util;

import javax.annotation.Nonnull;
import java.util.function.Function;

/**
 * Equivalent to {@link Function}, except with nonnull contract.
 * 
 * @see Function
 */
@FunctionalInterface
public interface NonNullFunction<T, R> extends io.github.fabricators_of_create.porting_lib.util.NonNullFunction<T, R>
{
}
