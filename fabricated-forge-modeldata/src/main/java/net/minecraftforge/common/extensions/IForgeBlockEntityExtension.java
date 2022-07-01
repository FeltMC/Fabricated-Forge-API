package net.minecraftforge.common.extensions;

import io.github.fabricators_of_create.porting_lib.extensions.BlockEntityExtensions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;

public interface IForgeBlockEntityExtension extends BlockEntityExtensions {
    private BlockEntity self() { return (BlockEntity) this; }
    @Override
    default void onLoad(){
        requestModelDataUpdate();
    }

    default IModelData getModelData(){
        return EmptyModelData.INSTANCE;
    }

    default void requestModelDataUpdate()
    {
        BlockEntity te = self();
        Level level = te.getLevel();
        if (level != null && level.isClientSide)
        {
            ModelDataManager.requestModelDataRefresh(te);
        }
    }
}
