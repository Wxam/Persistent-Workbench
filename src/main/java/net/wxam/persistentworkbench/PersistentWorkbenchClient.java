package net.wxam.persistentworkbench;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(value = PersistentWorkbench.MOD_ID, dist = Dist.CLIENT)
public class PersistentWorkbenchClient {

    public PersistentWorkbenchClient(IEventBus modEventBus, ModContainer modContainer) {
        // Client-side init komt later
    }
}