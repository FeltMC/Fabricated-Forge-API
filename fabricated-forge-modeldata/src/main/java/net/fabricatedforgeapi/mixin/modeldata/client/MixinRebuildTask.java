package net.fabricatedforgeapi.mixin.modeldata.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricatedforgeapi.modeldata.ChunkCompileTaskExtension;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Random;

@Debug(export = true)
@Mixin(targets = "net.minecraft.client.renderer.chunk.ChunkRenderDispatcher$RenderChunk$RebuildTask", priority = 1100)
public class MixinRebuildTask {
    @SuppressWarnings({"MixinAnnotationTarget", "InvalidMemberReference", "UnresolvedMixinReference"})
    @Redirect(method = "@FeltASM:MixinMethodHandler(redirect:hookChunkBuildTesselate)", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/block/BlockRenderDispatcher;renderBatched(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/BlockAndTintGetter;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLjava/util/Random;)Z"))
    private boolean injectCompile(BlockRenderDispatcher instance, BlockState state, BlockPos pos, BlockAndTintGetter level, PoseStack poseStack, VertexConsumer consumer, boolean checkSides, Random random){
        return instance.renderBatched(state, pos, level, poseStack, consumer, checkSides, random, ((ChunkCompileTaskExtension)this).getModelData(pos));
    }

    @SuppressWarnings({"MixinAnnotationTarget", "InvalidMemberReference"})
    @Inject(method = "@FeltASM:MixinMethodHandler(redirect:hookChunkBuildTesselate)", at = @At(value = "RETURN", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void injectCompile2(BlockRenderDispatcher renderManager, BlockState state, BlockPos pos, BlockAndTintGetter level, PoseStack poseStack, VertexConsumer consumer, boolean checkSides, Random random, CallbackInfoReturnable<Boolean> cir, BakedModel model, Vec3 vec3d){
        if (model instanceof IDynamicBakedModel model1 && model1.isVanillaAdapter()){
            cir.setReturnValue(renderManager.renderBatched(state, pos, level, poseStack, consumer, checkSides, random, ((ChunkCompileTaskExtension)this).getModelData(pos)));
        }
    }
}
