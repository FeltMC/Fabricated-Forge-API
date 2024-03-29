/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model;

import net.fabricatedforgeapi.modeldata.wrapper.PortingLibModelGeometryPart;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraftforge.client.model.geometry.IModelGeometryPart;

import javax.annotation.Nullable;

/*
 * Interface that provides generic access to the data within BlockModel,
 * while allowing non-blockmodel usage of models
 */
public interface IModelConfiguration extends io.github.fabricators_of_create.porting_lib.model.IModelConfiguration {

    /**
     * If available, gets the owning model (usually BlockModel) of this configuration
     */
    @Nullable
    UnbakedModel getOwnerModel();

    /**
     * @return The name of the model being baked, for logging and cache purposes.
     */
    String getModelName();

    /**
     * Checks if a texture is present in the model.
     * @param name The name of a texture channel.
     */
    boolean isTexturePresent(String name);

    /**
     * Resolves the final texture name, taking into account texture aliases and replacements.
     * @param name The name of a texture channel.
     * @return The location of the texture, or the missing texture if not found.
     */
    Material resolveTexture(String name);

    /**
     * @return True if the item is a 3D model, false if it's a generated item model.
     * TODO: Rename.
     * This value has nothing to do with shading anymore, and this name is misleading.
     * It's actual purpose seems to be relegated to translating the model during rendering, so that it's centered.
     */
    boolean isShadedInGui();

    /**
     * @return True if the item is lit from the side
     */
    boolean isSideLit();

    /**
     * @return True if the item requires per-vertex lighting.
     */
    boolean useSmoothLighting();

    /**
     * Gets the vanilla camera transforms data.
     * Do not use for non-vanilla code. For general usage, prefer getCombinedState.
     */
    ItemTransforms getCameraTransforms();

    /**
     * @return The combined transformation state including vanilla and forge transforms data.
     */
    ModelState getCombinedTransform();

    /**
     * Queries the visibility information for the model parts.
     * @param part A part for which to query visibility.
     * @param fallback A boolean specifying the default visibility if an override isn't found in the model data.
     * @return The final computed visibility.
     */
    default boolean getPartVisibility(IModelGeometryPart part, boolean fallback) {
        return fallback;
    }

    /**
     * Queries the visibility information for the model parts. Same as above, but defaulting to visible.
     * @param part A part for which to query visibility.
     * @return The final computed visibility.
     */
    default boolean getPartVisibility(IModelGeometryPart part) {
        return getPartVisibility(part, true);
    }

    default boolean getPartVisibility(io.github.fabricators_of_create.porting_lib.model.IModelGeometryPart part, boolean fallback) {
        if (part instanceof IModelGeometryPart geometryPart) {
            return getPartVisibility(geometryPart, fallback);
        }
        return getPartVisibility(new PortingLibModelGeometryPart(part), fallback);
    }



}
