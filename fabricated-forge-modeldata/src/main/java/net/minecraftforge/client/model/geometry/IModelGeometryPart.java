/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model.geometry;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.IModelBuilder;
import net.minecraftforge.client.model.IModelConfiguration;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;

public interface IModelGeometryPart extends io.github.fabricators_of_create.porting_lib.model.IModelGeometryPart
{
    String name();

    void addQuads(IModelConfiguration owner, IModelBuilder<?> modelBuilder, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ResourceLocation modelLocation);

    default Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
        // No texture dependencies
        return Collections.emptyList();
    }

    default Collection<Material> getTextures(io.github.fabricators_of_create.porting_lib.model.IModelConfiguration owner, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
        if (owner instanceof IModelConfiguration configuration){
            return getTextures(configuration, modelGetter, missingTextureErrors);
        }
        // No texture dependencies
        return Collections.emptyList();
    }

    @Override
    default void addQuads(io.github.fabricators_of_create.porting_lib.model.IModelConfiguration owner, io.github.fabricators_of_create.porting_lib.model.IModelBuilder<?> modelBuilder, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ResourceLocation modelLocation){
        if (owner instanceof IModelConfiguration configuration && modelBuilder instanceof IModelBuilder<?> builder){
            addQuads(configuration, builder, bakery, spriteGetter, modelTransform, modelLocation);
        }
    }
}
