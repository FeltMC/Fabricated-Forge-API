package net.fabricatedforgeapi.caps;

import net.fabricmc.api.ModInitializer;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;

public class CapsInitializer implements ModInitializer {
    @Override
    public void onInitialize() {
        //RegisterCapabilitiesEvent.REGISTER_CAPS.register(e -> net.minecraftforge.fml.ModLoader);
        CapabilityManager.INSTANCE.injectCapabilities();
    }
}
