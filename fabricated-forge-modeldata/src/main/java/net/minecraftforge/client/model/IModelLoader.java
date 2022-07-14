/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraftforge.client.model.geometry.IModelGeometry;

public interface IModelLoader<T extends IModelGeometry<T>> extends io.github.fabricators_of_create.porting_lib.model.IModelLoader<T>
{
    @Override
    T read(JsonDeserializationContext deserializationContext, JsonObject modelContents);
}
