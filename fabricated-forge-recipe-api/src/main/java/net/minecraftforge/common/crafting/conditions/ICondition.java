package net.minecraftforge.common.crafting.conditions;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.fabric.api.resource.conditions.v1.ConditionJsonProvider;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagKey;
import net.minecraftforge.common.crafting.CraftingHelper;

import java.util.Collections;
import java.util.Map;

public interface ICondition extends ConditionJsonProvider {
    ResourceLocation getID();

    @Override
    default ResourceLocation getConditionId(){
        return getID();
    }

    @Override
    default JsonObject toJson() {
        IConditionSerializer<ICondition> serializer = CraftingHelper.getSerializer(this.getID());
        if (serializer == null)
            throw new JsonSyntaxException("Unknown condition type: " + this.getID().toString());
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(ResourceConditions.CONDITION_ID_KEY, getConditionId().toString());
        jsonObject.addProperty("type", getID().toString());
        serializer.write(jsonObject, this);
        return jsonObject;
    }

    @Override
    default void writeParameters(JsonObject object){
    }

    default boolean test(IContext context)
    {
        return test();
    }

    /**
     * @deprecated Use {@linkplain #test(IContext) the other more general overload}.
     */
    @Deprecated(forRemoval = true, since = "1.18.2")
    boolean test();

    interface IContext
    {
        IContext EMPTY = new IContext()
        {
            @Override
            public <T> Map<ResourceLocation, Tag<Holder<T>>> getAllTags(ResourceKey<? extends Registry<T>> registry)
            {
                return Collections.emptyMap();
            }
        };

        /**
         * Return the requested tag if available, or an empty tag otherwise.
         */
        default <T> Tag<Holder<T>> getTag(TagKey<T> key)
        {
            return getAllTags(key.registry()).getOrDefault(key.location(), Tag.empty());
        }

        /**
         * Return all the loaded tags for the passed registry, or an empty map if none is available.
         * Note that the map and the tags are unmodifiable.
         */
        <T> Map<ResourceLocation, Tag<Holder<T>>> getAllTags(ResourceKey<? extends Registry<T>> registry);
    }
}
