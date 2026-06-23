package net.wxam.persistentworkbench;

import com.mojang.logging.LogUtils;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.wxam.persistentworkbench.block.ModBlocks;
import net.wxam.persistentworkbench.item.ModItems;
import net.wxam.persistentworkbench.menu.ModMenus;
import org.slf4j.Logger;

@Mod(PersistentWorkbench.MOD_ID)
public class PersistentWorkbench {

    public static final String MOD_ID = "persistentworkbench";
    public static final Logger LOGGER = LogUtils.getLogger();

    public PersistentWorkbench(IEventBus modEventBus, ModContainer modContainer) {
        ModBlocks.BLOCKS.register(modEventBus);
        ModBlocks.BLOCK_ENTITIES.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModMenus.MENUS.register(modEventBus);

        modEventBus.addListener(this::addCreative);

        LOGGER.info("Persistent Workbench initialized!");
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(ModItems.WORKBENCH.get());
            event.accept(ModItems.LINK_CRYSTAL_WAND.get());
            event.accept(ModItems.LINK_CRYSTAL);
        }
    }
}