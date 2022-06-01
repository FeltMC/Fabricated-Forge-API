package net.minecraftforge.mixin.crafting;

import com.google.gson.JsonElement;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IngredientExtension;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.stream.Stream;

@Debug(export = true)
@Mixin(Ingredient.class)
public abstract class IngredientMixin implements IngredientExtension {

    @Unique
    private int invalidationCounter;
    @Mutable
    @Unique
    @Final
    private boolean isSimple;

    @Shadow
    @Nullable
    private ItemStack[] itemStacks;

    @Shadow
    @Nullable
    private IntList stackingIds;

    @Shadow
    @Final
    public static Ingredient EMPTY;

    @Shadow
    @Final
    private Ingredient.Value[] values;

    @Shadow
    protected abstract void dissolve();

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    private void injectToInit(Stream stream, CallbackInfo ci){
        //this.isSimple = !net.minecraftforge.data.loading.DatagenModLoader.isRunningDataGen() && !Arrays.stream(values).anyMatch(list -> list.getItems().stream().anyMatch(stack -> stack.getItem().isDamageable(stack)));
    }

    /*@Inject(method = "getStackingIds", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/Ingredient;dissolve()V", shift = At.Shift.BEFORE))
    private void injectMarkValid(CallbackInfoReturnable<IntList> cir){
        this.markValid();
    }

    @Inject(method = "getStackingIds", at = @At("RETURN"), cancellable = true)
    private void injectStackingIds(CallbackInfoReturnable<IntList> cir){
        if (!checkInvalidation()){
            cir.setReturnValue(this.stackingIds);
        }
    }*/

    @Inject(method = "fromJson", at = @At(value = "INVOKE", target = "Lcom/google/gson/JsonElement;isJsonObject()Z", shift = At.Shift.BEFORE), cancellable = true)
    private static void injectFromJson(JsonElement json, CallbackInfoReturnable<Ingredient> cir){
        Ingredient ret = net.minecraftforge.common.crafting.CraftingHelper.getIngredient(json);
        if (ret != null)  cir.setReturnValue(ret);
    }

    /**
     * @author Trinsdar
     * @reason Could not find any other way to do what I wanted, unless I were to get into asm
     * Based off forge Ingredient
     */
    // TODO figure out how to do this without replacing the method
    @Overwrite
    public IntList getStackingIds() {
        if (this.stackingIds == null || checkInvalidation()) {
            this.markValid();
            this.dissolve();
            this.stackingIds = new IntArrayList(this.itemStacks.length);

            for(ItemStack itemstack : this.itemStacks) {
                this.stackingIds.add(StackedContents.getStackingIndex(itemstack));
            }

            this.stackingIds.sort(IntComparators.NATURAL_COMPARATOR);
        }

        return this.stackingIds;
    }

    @Inject(method = "toNetwork", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/Ingredient;dissolve()V", shift = At.Shift.AFTER), cancellable = true)
    private void injectToNetwork(FriendlyByteBuf buffer, CallbackInfo ci){
        if (!this.isVanilla()){
            net.minecraftforge.common.crafting.CraftingHelper.write(buffer, (Ingredient) (Object)this);
            ci.cancel();
        }
    }

    @Inject(method = "fromNetwork", at = @At("HEAD"), cancellable = true)
    private static void injectFromNetwork(FriendlyByteBuf buffer, CallbackInfoReturnable<Ingredient> cir){
        var size = buffer.readVarInt();
        if (size == -1) cir.setReturnValue(CraftingHelper.getIngredient(buffer.readResourceLocation(), buffer));
    }

    @Override
    public boolean checkInvalidation() {
        int currentInvalidationCounter = INVALIDATION_COUNTER.get();
        if (this.invalidationCounter != currentInvalidationCounter) {
            invalidate();
            return true;
        }
        return false;
    }

    @Override
    public void markValid() {
        this.invalidationCounter = INVALIDATION_COUNTER.get();
    }

    @Override
    public void invalidate() {
        this.itemStacks = null;
        this.stackingIds = null;
    }

    @Override
    public boolean isSimple() {
        return isSimple || ((Ingredient)(Object)this) == EMPTY;
    }

    @Unique
    private final boolean isVanilla = this.getClass().equals(Ingredient.class);
    @Override
    public boolean isVanilla() {
        return isVanilla;
    }

    public net.minecraftforge.common.crafting.IIngredientSerializer<? extends Ingredient> getSerializer() {
        if (!isVanilla()) throw new IllegalStateException("Modders must implement Ingredient.getSerializer in their custom Ingredients: " + this);
        return net.minecraftforge.common.crafting.VanillaIngredientSerializer.INSTANCE;
    }
}
