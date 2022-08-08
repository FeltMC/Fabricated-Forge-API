package net.minecraftforge.common.crafting;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public class FabricatedForgeRecipeApi implements ModInitializer {
    @Override
    public void onInitialize() {
        Registry.register(Registry.RECIPE_SERIALIZER, new ResourceLocation("forge", "conditional"), ConditionalRecipe.SERIALZIER);
    }
}
