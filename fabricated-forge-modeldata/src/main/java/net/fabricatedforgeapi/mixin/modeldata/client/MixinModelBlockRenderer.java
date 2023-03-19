package net.fabricatedforgeapi.mixin.modeldata.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricatedforgeapi.modeldata.ModelBlockRendererExtension;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.BitSet;
import java.util.List;
import java.util.Random;

import static net.minecraft.client.renderer.LevelRenderer.DIRECTIONS;

@Debug(export = true)
@Mixin(value = ModelBlockRenderer.class, priority = 1100)
public abstract class MixinModelBlockRenderer implements ModelBlockRendererExtension {
    @Shadow protected abstract void renderModelFaceFlat(BlockAndTintGetter level, BlockState state, BlockPos pos, int packedLight, int packedOverlay, boolean repackLight, PoseStack poseStack, VertexConsumer consumer, List<BakedQuad> quads, BitSet shapeFlags);

    @Shadow @Final private static Direction[] DIRECTIONS;

    @Shadow protected abstract void renderModelFaceAO(BlockAndTintGetter level, BlockState state, BlockPos pos, PoseStack poseStack, VertexConsumer consumer, List<BakedQuad> quads, float[] shape, BitSet shapeFlags, ModelBlockRenderer.AmbientOcclusionFace aoFace, int packedOverlay);

    @Shadow
    protected static void renderQuadList(PoseStack.Pose pose, VertexConsumer consumer, float red, float green, float blue, List<BakedQuad> quads, int packedLight, int packedOverlay) {
    }

    public boolean tesselateBlock(BlockAndTintGetter level, BakedModel model, BlockState state, BlockPos pos, PoseStack poseStack, VertexConsumer consumer, boolean checkSides, Random random, long seed, int packedOverlay, IModelData data) {
        boolean bl = Minecraft.useAmbientOcclusion() && state.getLightEmission() == 0 && model.useAmbientOcclusion();
        Vec3 vec3 = state.getOffset(level, pos);
        poseStack.translate(vec3.x, vec3.y, vec3.z);

        try {
            return bl ? this.tesselateWithAO(level, model, state, pos, poseStack, consumer, checkSides, random, seed, packedOverlay, data) : this.tesselateWithoutAO(level, model, state, pos, poseStack, consumer, checkSides, random, seed, packedOverlay, data);
        } catch (Throwable var17) {
            CrashReport crashReport = CrashReport.forThrowable(var17, "Tesselating block model");
            CrashReportCategory crashReportCategory = crashReport.addCategory("Block model being tesselated");
            CrashReportCategory.populateBlockDetails(crashReportCategory, level, pos, state);
            crashReportCategory.setDetail("Using AO", bl);
            throw new ReportedException(crashReport);
        }
    }

    public boolean tesselateWithAO(BlockAndTintGetter level, BakedModel model, BlockState state, BlockPos pos, PoseStack poseStack, VertexConsumer consumer, boolean checkSides, Random random, long seed, int packedOverlay, IModelData data) {
        boolean bl = false;
        float[] fs = new float[DIRECTIONS.length * 2];
        BitSet bitSet = new BitSet(3);
        ModelBlockRenderer.AmbientOcclusionFace ambientOcclusionFace = ((ModelBlockRenderer)(Object)this).new AmbientOcclusionFace();
        BlockPos.MutableBlockPos mutableBlockPos = pos.mutable();

        for (Direction dir : DIRECTIONS) {
            random.setSeed(seed);
            List<BakedQuad> list = model.getQuads(state, dir, random, data);
            if (!list.isEmpty()) {
                mutableBlockPos.setWithOffset(pos, dir);
                if (!checkSides || Block.shouldRenderFace(state, level, pos, dir, mutableBlockPos)) {
                    this.renderModelFaceAO(level, state, pos, poseStack, consumer, list, fs, bitSet, ambientOcclusionFace, packedOverlay);
                    bl = true;
                }
            }
        }

        random.setSeed(seed);
        List<BakedQuad> list2 = model.getQuads(state, (Direction)null, random, data);
        if (!list2.isEmpty()) {
            this.renderModelFaceAO(level, state, pos, poseStack, consumer, list2, fs, bitSet, ambientOcclusionFace, packedOverlay);
            bl = true;
        }

        return bl;
    }

    public boolean tesselateWithoutAO(BlockAndTintGetter level, BakedModel model, BlockState state, BlockPos pos, PoseStack poseStack, VertexConsumer consumer, boolean checkSides, Random random, long seed, int packedOverlay, IModelData data) {
        boolean bl = false;
        BitSet bitSet = new BitSet(3);
        BlockPos.MutableBlockPos mutableBlockPos = pos.mutable();

        for (Direction dir : DIRECTIONS){
            random.setSeed(seed);
            List<BakedQuad> list = model.getQuads(state, dir, random, data);
            if (!list.isEmpty()) {
                mutableBlockPos.setWithOffset(pos, dir);
                if (!checkSides || Block.shouldRenderFace(state, level, pos, dir, mutableBlockPos)) {
                    int i = LevelRenderer.getLightColor(level, state, mutableBlockPos);
                    this.renderModelFaceFlat(level, state, pos, i, packedOverlay, false, poseStack, consumer, list, bitSet);
                    bl = true;
                }
            }
        }

        random.setSeed(seed);
        List<BakedQuad> list2 = model.getQuads(state, (Direction)null, random, data);
        if (!list2.isEmpty()) {
            this.renderModelFaceFlat(level, state, pos, -1, packedOverlay, true, poseStack, consumer, list2, bitSet);
            bl = true;
        }

        return bl;
    }

    public void renderModel(PoseStack.Pose pose, VertexConsumer consumer, @org.jetbrains.annotations.Nullable BlockState state, BakedModel model, float red, float green, float blue, int packedLight, int packedOverlay, IModelData data) {
        Random random = new Random();
        for (Direction dir : DIRECTIONS) {
            random.setSeed(42L);
            renderQuadList(pose, consumer, red, green, blue, model.getQuads(state, dir, random, data), packedLight, packedOverlay);
        }
        random.setSeed(42L);
        renderQuadList(pose, consumer, red, green, blue, model.getQuads(state, (Direction)null, random, data), packedLight, packedOverlay);
    }
}
