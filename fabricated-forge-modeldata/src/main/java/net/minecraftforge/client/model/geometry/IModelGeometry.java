/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model.geometry;

import com.mojang.datafixers.util.Pair;
import net.fabricatedforgeapi.modeldata.wrapper.PortingLibModelConfiguration;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.IModelConfiguration;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * General interface for any model that can be baked, superset of vanilla {@link UnbakedModel}.
 * Models can be baked to different vertex formats and with different state.
 */
public interface IModelGeometry<T extends IModelGeometry<T>> extends io.github.fabricators_of_create.porting_lib.model.IModelGeometry<T>
{
    default Collection<? extends IModelGeometryPart> getParts() {
        return Collections.emptyList();
    }

    default Optional<? extends IModelGeometryPart> getPart(String name) {
        return Optional.empty();
    }

    BakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ItemOverrides overrides, ResourceLocation modelLocation);

    Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors);

    @Override
    default BakedModel bake(io.github.fabricators_of_create.porting_lib.model.IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ItemOverrides overrides, ResourceLocation modelLocation){
        if (owner instanceof IModelConfiguration configuration){
            return bake(configuration, bakery, spriteGetter, modelTransform, overrides, modelLocation);
        }
        owner = new PortingLibModelConfiguration(owner);
        return bake(((IModelConfiguration) owner), bakery, spriteGetter, modelTransform, overrides, modelLocation);
    }

    @Override
    default Collection<Material> getTextures(io.github.fabricators_of_create.porting_lib.model.IModelConfiguration owner, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors){
        if (owner instanceof IModelConfiguration configuration){
            return getTextures(configuration, modelGetter, missingTextureErrors);
        }
        owner = new PortingLibModelConfiguration(owner);
        return getTextures(((IModelConfiguration) owner), modelGetter, missingTextureErrors);
    }
}
