/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model.geometry;

import com.mojang.datafixers.util.Pair;
import net.fabricatedforgeapi.modeldata.wrapper.PortingLibModelBuilder;
import net.fabricatedforgeapi.modeldata.wrapper.PortingLibModelConfiguration;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
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

public interface ISimpleModelGeometry<T extends ISimpleModelGeometry<T>> extends IModelGeometry<T>, io.github.fabricators_of_create.porting_lib.model.ISimpleModelGeometry<T>
{
    @Override
    default BakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ItemOverrides overrides, ResourceLocation modelLocation)
    {
        TextureAtlasSprite particle = spriteGetter.apply(owner.resolveTexture("particle"));

        IModelBuilder<?> builder = IModelBuilder.of(owner, overrides, particle);

        addQuads(owner, builder, bakery, spriteGetter, modelTransform, modelLocation);

        return builder.build();
    }

    void addQuads(IModelConfiguration owner, IModelBuilder<?> modelBuilder, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ResourceLocation modelLocation);

    @Override
    Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors);

    @Override
    default Collection<Material> getTextures(io.github.fabricators_of_create.porting_lib.model.IModelConfiguration owner, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors){
        if (owner instanceof IModelConfiguration configuration){
            return getTextures(configuration, modelGetter, missingTextureErrors);
        }
        return getTextures(new PortingLibModelConfiguration(owner), modelGetter, missingTextureErrors);
    }

    @Override
    default BakedModel bake(io.github.fabricators_of_create.porting_lib.model.IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ItemOverrides overrides, ResourceLocation modelLocation){
        if (owner instanceof IModelConfiguration configuration){
            return bake(configuration, bakery, spriteGetter, modelTransform, overrides, modelLocation);
        }
        return bake(new PortingLibModelConfiguration(owner), bakery, spriteGetter, modelTransform, overrides, modelLocation);
    }

    @Override
    default void addQuads(io.github.fabricators_of_create.porting_lib.model.IModelConfiguration owner, io.github.fabricators_of_create.porting_lib.model.IModelBuilder<?> modelBuilder, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ResourceLocation modelLocation){
        IModelConfiguration configuration;
        IModelBuilder<?> builder;
        if (owner instanceof IModelConfiguration c) configuration = c;
        else configuration = new PortingLibModelConfiguration(owner);
        if (modelBuilder instanceof IModelBuilder<?> b) builder = b;
        else builder = new PortingLibModelBuilder(modelBuilder);
        addQuads(configuration, builder, bakery, spriteGetter, modelTransform, modelLocation);
    }
}
