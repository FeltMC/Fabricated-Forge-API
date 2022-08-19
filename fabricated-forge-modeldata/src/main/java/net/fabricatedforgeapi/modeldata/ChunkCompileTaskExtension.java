package net.fabricatedforgeapi.modeldata;

import net.minecraft.core.BlockPos;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;

public interface ChunkCompileTaskExtension {
    IModelData getModelData(BlockPos pos);
}
