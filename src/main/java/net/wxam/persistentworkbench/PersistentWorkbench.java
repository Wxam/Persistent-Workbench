package net.wxam.persistentworkbench;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.wxam.persistentworkbench.block.ModBlocks;
import net.wxam.persistentworkbench.item.ModItems;
import org.slf4j.Logger;

@Mod(PersistentWorkbench.MOD_ID)
public class PersistentWorkbench {

    public static final String MOD_ID = "persistentworkbench";
    public static final Logger LOGGER = LogUtils.getLogger();

    public PersistentWorkbench(IEventBus modEventBus, ModContainer modContainer) {
        ModBlocks.BLOCKS.register(modEventBus);
        ModBlocks.BLOCK_ENTITIES.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);

        LOGGER.info("Persistent Workbench initialized!");
    }
}