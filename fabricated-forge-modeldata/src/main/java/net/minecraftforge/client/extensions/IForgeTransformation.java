/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.extensions;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import io.github.fabricators_of_create.porting_lib.extensions.TransformationExtensions;
import net.minecraft.core.Direction;

public interface IForgeTransformation extends TransformationExtensions
{

    @Override
    default boolean isIdentity()
    {
        return TransformationExtensions.super.isIdentity();
    }

    @Override
    default void push(PoseStack stack)
    {
        TransformationExtensions.super.push(stack);

    }

    @Override
    default void transformPosition(Vector4f position)
    {
        TransformationExtensions.super.transformPosition(position);
    }

    @Override
    default void transformNormal(Vector3f normal)
    {
        TransformationExtensions.super.transformNormal(normal);
    }

    @Override
    default Direction rotateTransform(Direction facing)
    {
        return TransformationExtensions.super.rotateTransform(facing);
    }

    /**
     * convert transformation from assuming center-block system to opposing-corner-block system
     */
    @Override
    default Transformation blockCenterToCorner()
    {
        return TransformationExtensions.super.blockCenterToCorner();
    }

    /**
     * convert transformation from assuming opposing-corner-block system to center-block system
     */
    @Override
    default Transformation blockCornerToCenter()
    {
        return TransformationExtensions.super.blockCornerToCenter();
    }

    /**
     * Apply this transformation to a different origin.
     * Can be used for switching between coordinate systems.
     * Parameter is relative to the current origin.
     */
    @Override
    default Transformation applyOrigin(Vector3f origin) {
        return TransformationExtensions.super.applyOrigin(origin);
    }
}
