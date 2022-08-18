package net.fabricatedforgeapi.modeldata;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IModelData;

import java.util.Random;

public interface ModelBlockRendererExtension {
    default boolean tesselateBlock(BlockAndTintGetter level, BakedModel model, BlockState state, BlockPos pos, PoseStack poseStack, VertexConsumer consumer, boolean checkSides, Random random, long seed, int packedOverlay, IModelData data) {
        throw new RuntimeException("this should be overridden via mixin. what?");
    }

    default boolean tesselateWithAO(BlockAndTintGetter level, BakedModel model, BlockState state, BlockPos pos, PoseStack poseStack, VertexConsumer consumer, boolean checkSides, Random random, long seed, int packedOverlay, IModelData data) {
        throw new RuntimeException("this should be overridden via mixin. what?");
    }

    default boolean tesselateWithoutAO(BlockAndTintGetter level, BakedModel model, BlockState state, BlockPos pos, PoseStack poseStack, VertexConsumer consumer, boolean checkSides, Random random, long seed, int packedOverlay, IModelData data) {
        throw new RuntimeException("this should be overridden via mixin. what?");
    }

    default void renderModel(PoseStack.Pose pose, VertexConsumer consumer, @org.jetbrains.annotations.Nullable BlockState state, BakedModel model, float red, float green, float blue, int packedLight, int packedOverlay, IModelData data) {
        throw new RuntimeException("this should be overridden via mixin. what?");
    }
}
