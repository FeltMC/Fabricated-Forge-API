package net.fabricatedforgeapi.mixin.modeldata.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricatedforgeapi.modeldata.BlockRenderDispatcherExtension;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Random;

@Mixin(BlockRenderDispatcher.class)
public abstract class MixinBlockRenderDispatcher implements BlockRenderDispatcherExtension {
    @Shadow public abstract boolean renderBatched(BlockState state, BlockPos pos, BlockAndTintGetter level, PoseStack poseStack, VertexConsumer consumer, boolean checkSides, Random random);

    @Shadow public abstract void renderSingleBlock(BlockState state, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay);

    @Shadow public abstract void renderBreakingTexture(BlockState state, BlockPos pos, BlockAndTintGetter level, PoseStack poseStack, VertexConsumer consumer);

    @Unique private IModelData modelData = EmptyModelData.INSTANCE;
    @Unique private IModelData modelData1 = EmptyModelData.INSTANCE;
    @Unique private IModelData modelData2 = EmptyModelData.INSTANCE;

    @Redirect(method = "renderBatched", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/block/ModelBlockRenderer;tesselateBlock(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLjava/util/Random;JI)Z"))
    private boolean redirectRenderBatched(ModelBlockRenderer instance, BlockAndTintGetter level, BakedModel model, BlockState state, BlockPos pos, PoseStack poseStack, VertexConsumer consumer, boolean checkSides, Random random, long seed, int packedOverlay){
        return instance.tesselateBlock(level, model, state, pos, poseStack, consumer, checkSides, random, seed, packedOverlay, modelData);
    }

    @Redirect(method = "renderSingleBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/block/ModelBlockRenderer;renderModel(Lcom/mojang/blaze3d/vertex/PoseStack$Pose;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/client/resources/model/BakedModel;FFFII)V"))
    private void redirectRenderSingleBlock(ModelBlockRenderer instance, PoseStack.Pose pose, VertexConsumer consumer, BlockState state, BakedModel model, float red, float green, float blue, int packedLight, int packedOverlay){
        instance.renderModel(pose, consumer, state, model, red, green, blue, packedLight, packedOverlay, modelData1);
    }

    @Redirect(method = "renderBreakingTexture", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/block/ModelBlockRenderer;tesselateBlock(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLjava/util/Random;JI)Z"))
    private boolean redirectBreaking(ModelBlockRenderer instance, BlockAndTintGetter level, BakedModel model, BlockState state, BlockPos pos, PoseStack poseStack, VertexConsumer consumer, boolean checkSides, Random random, long seed, int packedOverlay){
        return instance.tesselateBlock(level, model, state, pos, poseStack, consumer, checkSides, random, seed, packedOverlay, modelData2);
    }

    @Override
    public boolean renderBatched(BlockState arg, BlockPos arg2, BlockAndTintGetter arg3, PoseStack arg4, VertexConsumer arg5, boolean bl, Random random, IModelData modelData) {
        this.modelData = modelData;
        boolean render = renderBatched(arg, arg2, arg3, arg4, arg5, bl, random);
        this.modelData = EmptyModelData.INSTANCE;
        return render;
    }

    @Override
    public void renderSingleBlock(BlockState arg, PoseStack arg2, MultiBufferSource arg3, int j, int k, IModelData modelData) {
        this.modelData1 = modelData;
        this.renderSingleBlock(arg, arg2, arg3, j, k);
        this.modelData1 = EmptyModelData.INSTANCE;
    }

    @Override
    public void renderBreakingTexture(BlockState arg, BlockPos arg2, BlockAndTintGetter arg3, PoseStack arg4, VertexConsumer arg5, IModelData modelData) {
        this.modelData2 = modelData;
        this.renderBreakingTexture(arg, arg2, arg3, arg4, arg5);
        this.modelData2 = EmptyModelData.INSTANCE;
    }
}
