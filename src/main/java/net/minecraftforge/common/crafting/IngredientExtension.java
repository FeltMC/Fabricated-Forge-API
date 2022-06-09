package net.minecraftforge.common.crafting;

import net.minecraft.world.item.crafting.Ingredient;

import java.util.concurrent.atomic.AtomicInteger;

public interface IngredientExtension {
    AtomicInteger INVALIDATION_COUNTER = new AtomicInteger();
    default boolean isVanilla(){
        return true;
    }

    default boolean isSimple(){
        return true;
    }

    default void invalidate(){}

    default void markValid(){}

    default boolean checkInvalidation(){
        return true;
    }

    default IIngredientSerializer<? extends Ingredient> getSerializer(){
        return null;
    }

    static void invalidateAll() {
        INVALIDATION_COUNTER.incrementAndGet();
    }
}
