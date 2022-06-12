package net.minecraftforge.mixin.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.extensions.IForgeItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Item.class)
public class ItemMixin implements IForgeItem {
    //Todo patch this and the Properties stuff in
    protected boolean canRepair;

    @Override
    public boolean isRepairable(ItemStack stack) {
        return canRepair && isDamageable(stack);
    }

    /**
     * @author Trinsdar
     */
    @Overwrite
    public boolean useOnRelease(ItemStack stack) {
        return stack.getItem() == Items.CROSSBOW;
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void injectForgeInit(Item.Properties properties, CallbackInfo ci){
        initClient();
    }

    // FORGE START
    private Object renderProperties;

    /*
       DO NOT CALL, IT WILL DISAPPEAR IN THE FUTURE
       Call RenderProperties.get instead
     */
    public Object getRenderPropertiesInternal() {
        return renderProperties;
    }

    //TODO bring proper datagen to fabric
    private void initClient() {
        // Minecraft instance isn't available in datagen, so don't call initializeClient if in datagen
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT && !isData()) {
            initializeClient(properties -> {
                if (properties == this)
                    throw new IllegalStateException("Don't extend IItemRenderProperties in your item, use an anonymous class instead.");
                this.renderProperties = properties;
            });
        }
    }

    private boolean isData(){
        //return net.minecraftforge.fml.loading.FMLLoader.getLaunchHandler().isData();
        return false;
    }

    public void initializeClient(java.util.function.Consumer<net.minecraftforge.client.IItemRenderProperties> consumer) {
    }
    // END FORGE
}
