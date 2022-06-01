package net.minecraftforge.common.crafting.conditions;

import net.minecraft.resources.ResourceLocation;

public interface ICondition {
    ResourceLocation getID();

    boolean test();
}
