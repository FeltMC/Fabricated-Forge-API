package net.fabricatedforgeapi.mixin.modeldata.client;

import net.fabricatedforgeapi.modeldata.ChunkCompileTaskExtension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.Map;

@Mixin(targets = "net.minecraft.client.renderer.chunk.ChunkRenderDispatcher.RenderChunk.ChunkCompileTask")
public class MixinChunkCompileTask implements ChunkCompileTaskExtension {
    @Unique
    protected Map<BlockPos, IModelData> modelData;

    @Inject(method = "<init>(Lnet/minecraft/client/renderer/chunk/ChunkRenderDispatcher$RenderChunk;DZ)V", at = @At("TAIL"))
    private void injectInit(ChunkRenderDispatcher.RenderChunk renderChunk, double d, boolean bl, CallbackInfo ci){
        BlockPos pos = renderChunk.getOrigin();
        if (pos == null) {
            this.modelData = Collections.emptyMap();
        } else {
            this.modelData = ModelDataManager.getModelData(Minecraft.getInstance().level, new ChunkPos(pos));
        }
    }

    public IModelData getModelData(BlockPos pos) {
        return this.modelData.getOrDefault(pos, EmptyModelData.INSTANCE);
    }
}
