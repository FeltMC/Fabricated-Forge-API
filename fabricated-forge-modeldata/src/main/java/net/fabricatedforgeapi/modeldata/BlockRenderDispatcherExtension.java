package net.fabricatedforgeapi.modeldata;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IModelData;

import java.util.Random;

public interface BlockRenderDispatcherExtension {

    default void renderBreakingTexture(BlockState arg, BlockPos arg2, BlockAndTintGetter arg3, PoseStack arg4, VertexConsumer arg5, IModelData modelData) {
        throw new RuntimeException("this should be overridden via mixin. what?");
    }

    default boolean renderBatched(BlockState arg, BlockPos arg2, BlockAndTintGetter arg3, PoseStack arg4, VertexConsumer arg5, boolean bl, Random random, IModelData modelData) {
        throw new RuntimeException("this should be overridden via mixin. what?");
    }

    default void renderSingleBlock(BlockState arg, PoseStack arg2, MultiBufferSource arg3, int j, int k, IModelData modelData) {
        throw new RuntimeException("this should be overridden via mixin. what?");
    }
}
