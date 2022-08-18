package net.fabricatedforgeapi.mixin.modeldata.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricatedforgeapi.modeldata.ModelBlockRendererExtension;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

@Debug(export = true)
@Mixin(value = ModelBlockRenderer.class, priority = 1100)
public abstract class MixinModelBlockRenderer implements ModelBlockRendererExtension {
    @Shadow public abstract boolean tesselateBlock(BlockAndTintGetter level, BakedModel model, BlockState state, BlockPos pos, PoseStack poseStack, VertexConsumer consumer, boolean checkSides, Random random, long seed, int packedOverlay);

    @Shadow public abstract boolean tesselateWithAO(BlockAndTintGetter level, BakedModel model, BlockState state, BlockPos pos, PoseStack poseStack, VertexConsumer consumer, boolean checkSides, Random random, long seed, int packedOverlay);

    @Shadow public abstract boolean tesselateWithoutAO(BlockAndTintGetter level, BakedModel model, BlockState state, BlockPos pos, PoseStack poseStack, VertexConsumer consumer, boolean checkSides, Random random, long seed, int packedOverlay);

    @Shadow public abstract void renderModel(PoseStack.Pose pose, VertexConsumer consumer, @org.jetbrains.annotations.Nullable BlockState state, BakedModel model, float red, float green, float blue, int packedLight, int packedOverlay);

    @Unique private IModelData modelData = EmptyModelData.INSTANCE;
    @Unique private IModelData modelData1 = EmptyModelData.INSTANCE;
    @Unique private IModelData modelData2 = EmptyModelData.INSTANCE;
    @Unique private IModelData modelData3 = EmptyModelData.INSTANCE;
    @Redirect(method = "tesselateBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/block/ModelBlockRenderer;tesselateWithoutAO(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLjava/util/Random;JI)Z"))
    private boolean redirectTesselateWithoutAO(ModelBlockRenderer instance, BlockAndTintGetter level, BakedModel model, BlockState state, BlockPos pos, PoseStack poseStack, VertexConsumer consumer, boolean checkSides, Random random, long seed, int packedOverlay){
        return tesselateWithoutAO(level, model, state, pos, poseStack, consumer, checkSides, random, seed, packedOverlay, modelData);
    }

    @Redirect(method = "tesselateBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/block/ModelBlockRenderer;tesselateWithAO(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLjava/util/Random;JI)Z"))
    private boolean redirectTesselateWithAO(ModelBlockRenderer instance, BlockAndTintGetter level, BakedModel model, BlockState state, BlockPos pos, PoseStack poseStack, VertexConsumer consumer, boolean checkSides, Random random, long seed, int packedOverlay){
        return tesselateWithAO(level, model, state, pos, poseStack, consumer, checkSides, random, seed, packedOverlay, modelData);
    }
    @Redirect(method = {"tesselateWithAO"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/BakedModel;getQuads(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Ljava/util/Random;)Ljava/util/List;"))
    private List<BakedQuad> redirectGetQuadsWithAO(BakedModel instance, BlockState state, Direction direction, Random random){
        return instance.getQuads(state, direction, random, this.modelData1);
    }

    @Redirect(method = {"tesselateWithoutAO"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/BakedModel;getQuads(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Ljava/util/Random;)Ljava/util/List;"))
    private List<BakedQuad> redirectGetQuadsWithoutAO(BakedModel instance, BlockState state, Direction direction, Random random){
        return instance.getQuads(state, direction, random, this.modelData2);
    }

    @Redirect(method = {"renderModel"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/BakedModel;getQuads(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Ljava/util/Random;)Ljava/util/List;"))
    private List<BakedQuad> redirectGetQuadsModel(BakedModel instance, BlockState state, Direction direction, Random random){
        return instance.getQuads(state, direction, random, this.modelData3);
    }
    public boolean tesselateBlock(BlockAndTintGetter level, BakedModel model, BlockState state, BlockPos pos, PoseStack poseStack, VertexConsumer consumer, boolean checkSides, Random random, long seed, int packedOverlay, IModelData modelData){
        modelData = model.getModelData(level, pos, state, modelData);
        this.modelData = modelData;
        boolean tesselate = tesselateBlock(level, model, state, pos, poseStack, consumer, checkSides, random, seed, packedOverlay);
        this.modelData = EmptyModelData.INSTANCE;
        return tesselate;
    }

    public boolean tesselateWithAO(BlockAndTintGetter level, BakedModel model, BlockState state, BlockPos pos, PoseStack poseStack, VertexConsumer consumer, boolean checkSides, Random random, long seed, int packedOverlay, IModelData data) {
        this.modelData1 = data;
        boolean tesselate = this.tesselateWithAO(level, model, state, pos, poseStack, consumer, checkSides, random, seed, packedOverlay);
        this.modelData1 = EmptyModelData.INSTANCE;
        return tesselate;
    }

    public boolean tesselateWithoutAO(BlockAndTintGetter level, BakedModel model, BlockState state, BlockPos pos, PoseStack poseStack, VertexConsumer consumer, boolean checkSides, Random random, long seed, int packedOverlay, IModelData data) {
        this.modelData2 = data;
        boolean tesselate = this.tesselateWithoutAO(level, model, state, pos, poseStack, consumer, checkSides, random, seed, packedOverlay);
        this.modelData2 = EmptyModelData.INSTANCE;
        return tesselate;
    }

    public void renderModel(PoseStack.Pose pose, VertexConsumer consumer, @Nullable BlockState state, BakedModel model, float red, float green, float blue, int packedLight, int packedOverlay, IModelData data) {
        this.modelData3 = data;
        this.renderModel(pose, consumer, state, model, red, green, blue, packedLight, packedOverlay);
        this.modelData3 = EmptyModelData.INSTANCE;
    }
}
