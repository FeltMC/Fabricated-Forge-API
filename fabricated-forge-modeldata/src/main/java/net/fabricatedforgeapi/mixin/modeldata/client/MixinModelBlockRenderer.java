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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

@Debug(export = true)
@Mixin(ModelBlockRenderer.class)
public abstract class MixinModelBlockRenderer implements ModelBlockRendererExtension {
    @Shadow public abstract boolean tesselateBlock(BlockAndTintGetter level, BakedModel model, BlockState state, BlockPos pos, PoseStack poseStack, VertexConsumer consumer, boolean checkSides, Random random, long seed, int packedOverlay);

    @Shadow public abstract boolean tesselateWithAO(BlockAndTintGetter level, BakedModel model, BlockState state, BlockPos pos, PoseStack poseStack, VertexConsumer consumer, boolean checkSides, Random random, long seed, int packedOverlay);

    @Shadow public abstract boolean tesselateWithoutAO(BlockAndTintGetter level, BakedModel model, BlockState state, BlockPos pos, PoseStack poseStack, VertexConsumer consumer, boolean checkSides, Random random, long seed, int packedOverlay);

    @Shadow public abstract void renderModel(PoseStack.Pose pose, VertexConsumer consumer, @org.jetbrains.annotations.Nullable BlockState state, BakedModel model, float red, float green, float blue, int packedLight, int packedOverlay);

    private IModelData modelData = EmptyModelData.INSTANCE;

    @Inject(method = "tesselateBlock", at = @At("HEAD"))
    private void injectTesselateBlock(BlockAndTintGetter level, BakedModel model, BlockState state, BlockPos pos, PoseStack poseStack, VertexConsumer consumer, boolean checkSides, Random random, long seed, int packedOverlay, CallbackInfoReturnable<Boolean> cir){
        modelData = model.getModelData(level, pos, state, modelData);
    }

    @Redirect(method = {"tesselateWithAO", "tesselateWithoutAO", "renderModel"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/BakedModel;getQuads(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Ljava/util/Random;)Ljava/util/List;"))
    private List<BakedQuad> redirectGetQuads(BakedModel instance, BlockState state, Direction direction, Random random){
        return instance.getQuads(state, direction, random, this.modelData);
    }

    public boolean tesselateBlock(BlockAndTintGetter level, BakedModel model, BlockState state, BlockPos pos, PoseStack poseStack, VertexConsumer consumer, boolean checkSides, Random random, long seed, int packedOverlay, IModelData modelData){

        this.modelData = modelData;
        return tesselateBlock(level, model, state, pos, poseStack, consumer, checkSides, random, seed, packedOverlay);
    }

    public boolean tesselateWithAO(BlockAndTintGetter level, BakedModel model, BlockState state, BlockPos pos, PoseStack poseStack, VertexConsumer consumer, boolean checkSides, Random random, long seed, int packedOverlay, IModelData data) {
        this.modelData = data;
        return this.tesselateWithAO(level, model, state, pos, poseStack, consumer, checkSides, random, seed, packedOverlay);
    }

    public boolean tesselateWithoutAO(BlockAndTintGetter level, BakedModel model, BlockState state, BlockPos pos, PoseStack poseStack, VertexConsumer consumer, boolean checkSides, Random random, long seed, int packedOverlay, IModelData data) {
        this.modelData = data;
        return this.tesselateWithoutAO(level, model, state, pos, poseStack, consumer, checkSides, random, seed, packedOverlay);
    }

    public void renderModel(PoseStack.Pose pose, VertexConsumer consumer, @Nullable BlockState state, BakedModel model, float red, float green, float blue, int packedLight, int packedOverlay, IModelData data) {
        this.modelData = data;
        this.renderModel(pose, consumer, state, model, red, green, blue, packedLight, packedOverlay);
    }
}
