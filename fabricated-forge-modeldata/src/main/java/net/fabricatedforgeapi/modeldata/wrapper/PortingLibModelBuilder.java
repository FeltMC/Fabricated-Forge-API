package net.fabricatedforgeapi.modeldata.wrapper;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraftforge.client.model.IModelBuilder;

public record PortingLibModelBuilder(io.github.fabricators_of_create.porting_lib.model.IModelBuilder<?> builder) implements IModelBuilder<PortingLibModelBuilder> {
    @Override
    public PortingLibModelBuilder addFaceQuad(Direction facing, BakedQuad quad) {
        builder.addFaceQuad(facing, quad);
        return this;
    }

    @Override
    public PortingLibModelBuilder addGeneralQuad(BakedQuad quad) {
        builder.addGeneralQuad(quad);
        return this;
    }

    @Override
    public BakedModel build() {
        return builder.build();
    }
}
