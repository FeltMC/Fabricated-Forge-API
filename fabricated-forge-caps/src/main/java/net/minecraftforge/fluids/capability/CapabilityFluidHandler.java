package net.minecraftforge.fluids.capability;

import net.minecraftforge.common.capabilities.Capability;

public class CapabilityFluidHandler {
    public static Capability<IFluidHandler> FLUID_HANDLER_CAPABILITY = new Capability<>("fluid_handler_capability");
    public static Capability<IFluidHandlerItem> FLUID_HANDLER_ITEM_CAPABILITY = new Capability<>("fluid_handler_item_capability");
}
