package net.fabricatedforgeapi.mixin.modeldata.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricatedforgeapi.modeldata.BlockRenderDispatcherExtension;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Random;

@Mixin(BlockRenderDispatcher.class)
public abstract class MixinBlockRenderDispatcher implements BlockRenderDispatcherExtension {
    @Shadow @Final private ModelBlockRenderer modelRenderer;

    @Shadow public abstract BakedModel getBlockModel(BlockState state);

    @Shadow @Final private BlockColors blockColors;
    @Shadow @Final private BlockEntityWithoutLevelRenderer blockEntityRenderer;
    @Shadow @Final private BlockModelShaper blockModelShaper;
    @Shadow @Final private Random random;

    @Override
    public boolean renderBatched(BlockState state, BlockPos pos, BlockAndTintGetter level, PoseStack poseStack, VertexConsumer consumer, boolean checkSides, Random random, IModelData modelData) {
        try {
            RenderShape renderShape = state.getRenderShape();
            return renderShape == RenderShape.MODEL && this.modelRenderer.tesselateBlock(level, this.getBlockModel(state), state, pos, poseStack, consumer, checkSides, random, state.getSeed(pos), OverlayTexture.NO_OVERLAY, modelData);
        } catch (Throwable var11) {
            CrashReport crashReport = CrashReport.forThrowable(var11, "Tesselating block in world");
            CrashReportCategory crashReportCategory = crashReport.addCategory("Block being tesselated");
            CrashReportCategory.populateBlockDetails(crashReportCategory, level, pos, state);
            throw new ReportedException(crashReport);
        }
    }

    public void renderSingleBlock(BlockState state, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, IModelData data) {
        RenderShape renderShape = state.getRenderShape();
        if (renderShape != RenderShape.INVISIBLE) {
            switch (renderShape) {
                case MODEL:
                    BakedModel bakedModel = this.getBlockModel(state);
                    int i = this.blockColors.getColor(state, (BlockAndTintGetter)null, (BlockPos)null, 0);
                    float f = (float)(i >> 16 & 255) / 255.0F;
                    float g = (float)(i >> 8 & 255) / 255.0F;
                    float h = (float)(i & 255) / 255.0F;
                    this.modelRenderer.renderModel(poseStack.last(), bufferSource.getBuffer(ItemBlockRenderTypes.getRenderType(state, false)), state, bakedModel, f, g, h, packedLight, packedOverlay, data);
                    break;
                case ENTITYBLOCK_ANIMATED:
                    this.blockEntityRenderer.renderByItem(new ItemStack(state.getBlock()), ItemTransforms.TransformType.NONE, poseStack, bufferSource, packedLight, packedOverlay);
            }

        }
    }

    public void renderBreakingTexture(BlockState state, BlockPos pos, BlockAndTintGetter level, PoseStack poseStack, VertexConsumer consumer, IModelData modelData) {
        if (state.getRenderShape() == RenderShape.MODEL) {
            BakedModel bakedModel = this.blockModelShaper.getBlockModel(state);
            long l = state.getSeed(pos);
            this.modelRenderer.tesselateBlock(level, bakedModel, state, pos, poseStack, consumer, true, this.random, l, OverlayTexture.NO_OVERLAY, modelData);
        }
    }
}
