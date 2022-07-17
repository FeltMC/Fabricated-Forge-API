package net.fabricatedforgeapi.mixin.datagen;

import net.minecraft.core.Registry;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagKey;
import net.minecraftforge.common.extensions.IForgeTagAppender;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TagsProvider.TagAppender.class)
public abstract class TagAppenderMixin<T> implements IForgeTagAppender<T> {
    @Shadow @Final private Tag.Builder builder;

    @Shadow @Final private String source;

    @Shadow @Final private Registry<T> registry;

    @Shadow public abstract TagsProvider.TagAppender<T> addTag(TagKey<T> tag);

    @Override
    public Tag.Builder getInternalBuilder() {
        return this.builder;
    }

    @Override
    public String getModID() {
        return this.source;
    }

    @Override
    public Registry<T> getRegistry() {
        return this.registry;
    }

    @Override
    public TagsProvider.TagAppender<T> addTags(TagKey<T>... values) {
        for (TagKey<T> value : values) {
            this.addTag(value);
        }
        return (TagsProvider.TagAppender<T>) (Object)this;
    }
}
