/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model.data;

import javax.annotation.Nullable;

public interface IModelData extends io.github.fabricators_of_create.porting_lib.model.IModelData
{
    /**
     * Check if this data has a property, even if the value is {@code null}. Can be
     * used by code that intends to fill in data for a render pipeline, such as the
     * forge animation system.
     * <p>
     * IMPORTANT: {@link #getData(ModelProperty)} <em>can</em> return {@code null}
     * even if this method returns {@code true}.
     * 
     * @param prop The property to check for inclusion in this model data
     * @return {@code true} if this data has the given property, even if no value is present
     */
    boolean hasProperty(ModelProperty<?> prop);

    @Nullable
    <T> T getData(ModelProperty<T> prop);
    
    @Nullable
    <T> T setData(ModelProperty<T> prop, T data);

    default boolean hasProperty(io.github.fabricators_of_create.porting_lib.model.ModelProperty<?> prop){
        return prop instanceof ModelProperty<?> property && hasProperty(property);
    }

    default @org.jetbrains.annotations.Nullable <T> T getData(io.github.fabricators_of_create.porting_lib.model.ModelProperty<T> prop){
        return prop instanceof ModelProperty<T> property ? getData(property) : null;
    }

    default @org.jetbrains.annotations.Nullable <T> T setData(io.github.fabricators_of_create.porting_lib.model.ModelProperty<T> prop, T data){
        return prop instanceof ModelProperty<T> property ? setData(property, data) : null;
    }
}
