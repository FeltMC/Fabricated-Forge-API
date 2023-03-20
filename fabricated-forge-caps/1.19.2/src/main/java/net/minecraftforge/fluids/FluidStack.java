/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fluids;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.fabricators_of_create.porting_lib.extensions.FluidExtensions;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.Optional;

@SuppressWarnings({"UnstableApiUsage"})
public class FluidStack {

    private io.github.fabricators_of_create.porting_lib.util.FluidStack portingLibStack = io.github.fabricators_of_create.porting_lib.util.FluidStack.EMPTY;

    public static final Codec<FluidStack> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Registry.FLUID.byNameCodec().fieldOf("FluidName").forGetter(FluidStack::getFluid),
                    Codec.LONG.fieldOf("Amount").forGetter(FluidStack::getRealAmount),
                    CompoundTag.CODEC.optionalFieldOf("VariantTag", null).forGetter(fluidStack -> fluidStack.getType().copyNbt()),
                    CompoundTag.CODEC.optionalFieldOf("Tag").forGetter(stack -> Optional.ofNullable(stack.getTag()))
            ).apply(instance, (fluid, amount, variantTag, tag) -> {
                FluidStack stack = new FluidStack(fluid, amount, variantTag);
                tag.ifPresent(stack::setTag);
                return stack;
            })
    );

    public static final FluidStack EMPTY = new FluidStack(FluidVariant.blank(), 0) {

        @Override
        public void setAmount(long amount) {
        }

        @Override
        public void shrink(int amount) {
        }

        @Override
        public void shrink(long amount) {
        }

        @Override
        public FluidStack copy() {
            return this;
        }
    };

    private final FluidVariant type;
    @Nullable
    private CompoundTag tag;
    private long amount;

    public FluidStack(FluidVariant type, long amount) {
        this.type = type;
        this.amount = amount;
        this.tag = type.copyNbt();
    }

    public FluidStack(FluidVariant type, long amount, @Nullable CompoundTag tag) {
        this(type, amount);
        this.tag = tag;
    }

    /**
     * Avoid this constructor when possible, may result in NBT loss
     */
    public FluidStack(Fluid type, long amount) {
        this(FluidVariant.of(type instanceof FlowingFluid flowing ? flowing.getSource() : type), amount);
    }

    public FluidStack(Fluid type, long amount, @Nullable CompoundTag nbt) {
        this(FluidVariant.of(type instanceof FlowingFluid flowing ? flowing.getSource() : type, nbt), amount);
        this.tag = nbt;
    }

    public FluidStack(Fluid type, int amount) {
        this(type, amount * 81L);
    }

    public FluidStack(Fluid type, int amount, @Nullable CompoundTag nbt) {
        this(type, amount * 81L, nbt);
    }

    public FluidStack(FluidStack copy, long amount) {
        this(copy.getType(), amount);
        if (copy.hasTag()) tag = copy.getTag().copy();
    }

    public void setAmount(long amount) {
        this.amount = amount;
        this.portingLibStack.setAmount(amount);
    }

    public void setAmount(int amount) {
        setAmount(amount * 81L);
    }

    public void grow(int amount){
        grow(amount * 81L);
    }

    public void grow(long amount) {
        setAmount(getRealAmount() + amount);
    }

    public FluidVariant getType() {
        return type;
    }

    public Fluid getFluid() {
        return getType().getFluid();
    }

    public long getRealAmount(){
        return amount;
    }

    public int getAmount() {
        return (int) (amount / 81);
    }

    public boolean isEmpty() {
        return amount <= 0 || getType().isBlank();
    }

    public void shrink(int amount) {
        shrink(amount * 81L);
    }

    public void shrink(long amount) {
        setAmount(getRealAmount() - amount);
    }

    public boolean isFluidEqual(FluidStack other) {
        if (this == other) return true;
        if (other == null) return false;

        FluidVariant mine = getType();
        FluidVariant theirs = other.getType();
        boolean fluidsEqual = mine.isOf(theirs.getFluid());

        CompoundTag myTag = mine.getNbt();
        CompoundTag theirTag = theirs.getNbt();
        boolean tagsEqual = Objects.equals(myTag, theirTag);

        return fluidsEqual && tagsEqual;
    }

    public boolean canFill(FluidVariant var) {
        return isEmpty() || var.isOf(getFluid()) && Objects.equals(var.getNbt(), getType().getNbt());
    }

    public CompoundTag writeToNBT(CompoundTag nbt) {
        nbt.put("Variant", getType().toNbt());
        nbt.putLong("Amount", getRealAmount());
        if (tag != null)
            nbt.put("Tag", tag);
        return nbt;
    }

    public static FluidStack loadFluidStackFromNBT(CompoundTag tag) {
        FluidStack stack;
        if (tag.contains("FluidName")) {
            Fluid fluid = Registry.FLUID.get(new ResourceLocation(tag.getString("FluidName")));
            int amount = tag.getInt("Amount");
            if (tag.contains("Tag")) {
                stack = new FluidStack(fluid, amount, tag.getCompound("Tag"));
            } else {
                stack = new FluidStack(fluid, amount);
            }
        } else {
            CompoundTag fluidTag = tag.getCompound("Variant");
            FluidVariant fluid = FluidVariant.fromNbt(fluidTag);
            stack = new FluidStack(fluid, tag.getLong("Amount"));
            if(tag.contains("Tag", Tag.TAG_COMPOUND))
                stack.tag = tag.getCompound("Tag");
        }

        return stack;
    }

    public Component getDisplayName() {
        return ((FluidExtensions)getFluid()).getAttributes().getDisplayName(this.toPortingLibStack());
    }

    public String getTranslationKey() {
        return ((FluidExtensions)getFluid()).getAttributes().getTranslationKey(this.toPortingLibStack());
    }

    public void setTag(CompoundTag tag) {
        this.tag = tag;
    }

    @Nullable
    public CompoundTag getTag() {
        return tag;
    }

    public CompoundTag getOrCreateTag() {
        if (tag == null) tag = new CompoundTag();
        return tag;
    }

    public void removeChildTag(String key) {
        if (getTag() == null) return;
        getTag().remove(key);
    }

    public boolean hasTag() {
        return tag != null;
    }

    public static FluidStack readFromPacket(FriendlyByteBuf buffer) {
        FluidVariant fluid = FluidVariant.fromPacket(buffer);
        long amount = buffer.readVarLong();
        CompoundTag tag = buffer.readNbt();
        if (fluid.isBlank()) return EMPTY;
        return new FluidStack(fluid, amount, tag);
    }

    public void writeToPacket(FriendlyByteBuf buffer) {
        writeToPacket(this, buffer);
    }

    public static void writeToPacket(FluidStack stack, FriendlyByteBuf buffer) {
        stack.getType().toPacket(buffer);
        buffer.writeVarLong(stack.getRealAmount());
        buffer.writeNbt(stack.tag);
    }

    public FluidStack copy() {
        return new FluidStack(FluidVariant.of(getFluid(), getType().copyNbt()), getRealAmount(), getTag());
    }

    private boolean isFluidStackTagEqual(FluidStack other) {
        return this.tag == null ? other.tag == null : other.tag != null && this.tag.equals(other.tag);
    }

    public static boolean areFluidStackTagsEqual(@Nonnull FluidStack stack1, @Nonnull FluidStack stack2) {
        return stack1.isFluidStackTagEqual(stack2);
    }

    public boolean containsFluid(@Nonnull FluidStack other) {
        return this.isFluidEqual(other) && this.amount >= other.amount;
    }

    public boolean isFluidStackIdentical(FluidStack other) {
        return this.isFluidEqual(other) && this.amount == other.amount;
    }

    //TODO: figure out how fabric stores fluids in items
    /*public boolean isFluidEqual(@Nonnull ItemStack other) {
        return (Boolean) FluidUtil.getFluidContained(other).map(this::isFluidEqual).orElse(false);
    }*/

    public final boolean equals(Object o) {
        return o instanceof FluidStack fs && this.isFluidEqual(fs);
    }

    public io.github.fabricators_of_create.porting_lib.util.FluidStack toPortingLibStack(){
        if (portingLibStack.isEmpty() || portingLibStack.getAmount() != this.getAmount()){
            portingLibStack = new io.github.fabricators_of_create.porting_lib.util.FluidStack(this.type, this.amount);
        }
        return portingLibStack;
    }

    public static FluidStack fromPortingLibStack(io.github.fabricators_of_create.porting_lib.util.FluidStack stack){
        return new FluidStack(stack.getType(), stack.getAmount());
    }
}
