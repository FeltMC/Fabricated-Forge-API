package net.fabricatedforgeapi.mixin.modeldata.client.sodium;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import me.jellysquid.mods.sodium.client.gl.compile.ChunkBuildContext;
import me.jellysquid.mods.sodium.client.render.chunk.RenderSection;
import me.jellysquid.mods.sodium.client.render.chunk.compile.ChunkBuildResult;
import me.jellysquid.mods.sodium.client.render.chunk.compile.buffers.ChunkModelBuilder;
import me.jellysquid.mods.sodium.client.render.chunk.tasks.ChunkRenderRebuildTask;
import me.jellysquid.mods.sodium.client.render.pipeline.BlockRenderer;
import me.jellysquid.mods.sodium.client.util.task.CancellationSource;
import net.fabricatedforgeapi.modeldata.sodium.BlockRendererExtension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;

@Mixin(ChunkRenderRebuildTask.class)
public class MixinChunkRenderRebuildTask {
    @Shadow @Final private RenderSection render;
    @Unique
    private Map<BlockPos, IModelData> modelDataMap = new HashMap<>();

    @Inject(method = "performBuild", at =@At("HEAD"))
    private void injectModelDataMap(ChunkBuildContext buildContext, CancellationSource cancellationSource, CallbackInfoReturnable<ChunkBuildResult> cir){
        this.modelDataMap = ModelDataManager.getModelData(Minecraft.getInstance().level, new ChunkPos(SectionPos.blockToSectionCoord(this.render.getOriginX()), SectionPos.blockToSectionCoord(this.render.getOriginZ())));
    }

    @Redirect(method = "performBuild", at = @At(value = "INVOKE", target = "Lme/jellysquid/mods/sodium/client/render/pipeline/BlockRenderer;renderModel(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;Lnet/minecraft/client/resources/model/BakedModel;Lme/jellysquid/mods/sodium/client/render/chunk/compile/buffers/ChunkModelBuilder;ZJ)Z"))
    private boolean redirectRenderModel(BlockRenderer renderer, BlockAndTintGetter slice, BlockState state, BlockPos blockPos, BlockPos offset, BakedModel model, ChunkModelBuilder buffers, boolean cull, long seed){
        return ((BlockRendererExtension)renderer).renderModel(slice, state, blockPos, offset, model, buffers, cull, seed, modelDataMap.getOrDefault(blockPos, EmptyModelData.INSTANCE));
    }
}
