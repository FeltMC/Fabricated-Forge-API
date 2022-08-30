package net.fabricatedforgeapi.mixin.modeldata.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricatedforgeapi.modeldata.ChunkCompileTaskExtension;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Random;

@Debug(export = true)
@Mixin(targets = "net.minecraft.client.renderer.chunk.ChunkRenderDispatcher$RenderChunk$RebuildTask", priority = 1100)
public class MixinRebuildTask {
    @SuppressWarnings({"MixinAnnotationTarget", "InvalidMemberReference", "UnresolvedMixinReference"})
    @Redirect(method = "@FeltASM:RedirectHandler(hookChunkBuildTesselate)", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/block/BlockRenderDispatcher;renderBatched(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/BlockAndTintGetter;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLjava/util/Random;)Z"))
    private boolean injectCompile(BlockRenderDispatcher instance, BlockState state, BlockPos pos, BlockAndTintGetter level, PoseStack poseStack, VertexConsumer consumer, boolean checkSides, Random random){
        return instance.renderBatched(state, pos, level, poseStack, consumer, checkSides, random, ((ChunkCompileTaskExtension)this).getModelData(pos));
    }
}
