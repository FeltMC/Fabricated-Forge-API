package net.fabricatedforgeapi.modeldata.sodium;

import me.jellysquid.mods.sodium.client.render.chunk.compile.buffers.ChunkModelBuilder;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IModelData;

public interface BlockRendererExtension {
    boolean renderModel(BlockAndTintGetter world, BlockState state, BlockPos pos, BlockPos origin, BakedModel model, ChunkModelBuilder buffers, boolean cull, long seed, IModelData data);
}
