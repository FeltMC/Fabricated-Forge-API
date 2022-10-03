package net.fabricatedforgeapi.mixin.modeldata.client.sodium;

import me.jellysquid.mods.sodium.client.render.chunk.compile.buffers.ChunkModelBuilder;
import me.jellysquid.mods.sodium.client.render.pipeline.BlockRenderer;
import net.fabricatedforgeapi.modeldata.sodium.BlockRendererExtension;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.Random;

@Mixin(value = BlockRenderer.class)
public abstract class MixinBlockRenderer implements BlockRendererExtension {
    private IModelData modelData = EmptyModelData.INSTANCE;

    @Shadow public abstract boolean renderModel(BlockAndTintGetter world, BlockState state, BlockPos pos, BlockPos origin, BakedModel model, ChunkModelBuilder buffers, boolean cull, long seed);

    @Override
    public boolean renderModel(BlockAndTintGetter world, BlockState state, BlockPos pos, BlockPos origin, BakedModel model, ChunkModelBuilder buffers, boolean cull, long seed, IModelData data) {
        this.modelData = data;
        modelData = model.getModelData(world, pos, state, modelData);
        boolean render = renderModel(world, state, pos, origin, model, buffers, cull, seed);
        this.modelData = EmptyModelData.INSTANCE;
        return render;
    }


    @Redirect(method = {"renderModel"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/BakedModel;getQuads(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Ljava/util/Random;)Ljava/util/List;"))
    private List<BakedQuad> redirectGetQuadsModel(BakedModel instance, BlockState state, Direction direction, Random random){
        return instance.getQuads(state, direction, random, this.modelData);
    }
}
