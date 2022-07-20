package net.fabricatedforgeapi.mixin.modeldata.sodium;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import me.jellysquid.mods.sodium.client.model.quad.ModelQuadView;
import me.jellysquid.mods.sodium.client.model.vertex.VanillaVertexTypes;
import me.jellysquid.mods.sodium.client.model.vertex.VertexDrain;
import me.jellysquid.mods.sodium.client.model.vertex.formats.quad.QuadVertexSink;
import me.jellysquid.mods.sodium.client.render.texture.SpriteUtil;
import me.jellysquid.mods.sodium.client.util.ModelQuadUtil;
import me.jellysquid.mods.sodium.client.util.color.ColorABGR;
import me.jellysquid.mods.sodium.client.util.rand.XoRoShiRoRandom;
import me.jellysquid.mods.sodium.common.util.DirectionUtil;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

@Mixin(value = ModelBlockRenderer.class, priority = 900)
public abstract class MixinModelBlockRenderer {
    @Unique
    private final XoRoShiRoRandom random = new XoRoShiRoRandom(42L);

    @Shadow
    public abstract boolean tesselateBlock(BlockAndTintGetter level, BakedModel model, BlockState state, BlockPos pos, PoseStack poseStack, VertexConsumer consumer, boolean checkSides, Random random, long seed, int packedOverlay);

    @Shadow public abstract boolean tesselateWithAO(BlockAndTintGetter level, BakedModel model, BlockState state, BlockPos pos, PoseStack poseStack, VertexConsumer consumer, boolean checkSides, Random random, long seed, int packedOverlay);

    @Shadow public abstract boolean tesselateWithoutAO(BlockAndTintGetter level, BakedModel model, BlockState state, BlockPos pos, PoseStack poseStack, VertexConsumer consumer, boolean checkSides, Random random, long seed, int packedOverlay);

    private IModelData modelData = EmptyModelData.INSTANCE;

    @Inject(method = "tesselateBlock", at = @At("HEAD"))
    private void injectTesselateBlock(BlockAndTintGetter level, BakedModel model, BlockState state, BlockPos pos, PoseStack poseStack, VertexConsumer consumer, boolean checkSides, Random random, long seed, int packedOverlay, CallbackInfoReturnable<Boolean> cir){
        modelData = model.getModelData(level, pos, state, modelData);
    }

    @Redirect(method = {"tesselateWithAO", "tesselateWithoutAO"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/BakedModel;getQuads(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Ljava/util/Random;)Ljava/util/List;"))
    private List<BakedQuad> redirectGetQuads(BakedModel instance, BlockState state, Direction direction, Random random){
        return instance.getQuads(state, direction, random, this.modelData);
    }

    /**
     * @reason Use optimized vertex writer intrinsics, avoid allocations
     * @author JellySquid
     */
    @Overwrite
    public void renderModel(PoseStack.Pose entry, VertexConsumer vertexConsumer, BlockState blockState, BakedModel bakedModel, float red, float green, float blue, int light, int overlay) {
        QuadVertexSink drain = VertexDrain.of(vertexConsumer)
                .createSink(VanillaVertexTypes.QUADS);
        XoRoShiRoRandom random = this.random;

        // Clamp color ranges
        red = Mth.clamp(red, 0.0F, 1.0F);
        green = Mth.clamp(green, 0.0F, 1.0F);
        blue = Mth.clamp(blue, 0.0F, 1.0F);

        int defaultColor = ColorABGR.pack(red, green, blue, 1.0F);

        for (Direction direction : DirectionUtil.ALL_DIRECTIONS) {
            random.setSeed(42L);
            List<BakedQuad> quads = bakedModel.getQuads(blockState, direction, random, this.modelData);

            if (!quads.isEmpty()) {
                renderQuad(entry, drain, defaultColor, quads, light, overlay);
            }
        }

        random.setSeed(42L);
        List<BakedQuad> quads = bakedModel.getQuads(blockState, null, random, this.modelData);

        if (!quads.isEmpty()) {
            renderQuad(entry, drain, defaultColor, quads, light, overlay);
        }

        drain.flush();
    }

    private static void renderQuad(PoseStack.Pose entry, QuadVertexSink drain, int defaultColor, List<BakedQuad> list, int light, int overlay) {
        if (list.isEmpty()) {
            return;
        }

        drain.ensureCapacity(list.size() * 4);

        for (BakedQuad bakedQuad : list) {
            int color = bakedQuad.isTinted() ? defaultColor : 0xFFFFFFFF;

            ModelQuadView quad = ((ModelQuadView) bakedQuad);

            for (int i = 0; i < 4; i++) {
                drain.writeQuad(entry, quad.getX(i), quad.getY(i), quad.getZ(i), color, quad.getTexU(i), quad.getTexV(i),
                        light, overlay, ModelQuadUtil.getFacingNormal(bakedQuad.getDirection()));
            }

            SpriteUtil.markSpriteActive(quad.getSprite());
        }
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
