package net.fabricatedforgeapi.modeldata.wrapper;

import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraftforge.client.model.IModelConfiguration;
import org.jetbrains.annotations.Nullable;

public record PortingLibModelConfiguration(io.github.fabricators_of_create.porting_lib.model.IModelConfiguration config) implements IModelConfiguration {
    @Nullable
    @Override
    public UnbakedModel getOwnerModel() {
        return config.getOwnerModel();
    }

    @Override
    public String getModelName() {
        return config.getModelName();
    }

    @Override
    public boolean isTexturePresent(String name) {
        return config.isTexturePresent(name);
    }

    @Override
    public Material resolveTexture(String name) {
        return config.resolveTexture(name);
    }

    @Override
    public boolean isShadedInGui() {
        return config.isShadedInGui();
    }

    @Override
    public boolean isSideLit() {
        return config.isSideLit();
    }

    @Override
    public boolean useSmoothLighting() {
        return config.useSmoothLighting();
    }

    @Override
    public ItemTransforms getCameraTransforms() {
        return config.getCameraTransforms();
    }

    @Override
    public ModelState getCombinedTransform() {
        return config.getCombinedTransform();
    }
}
