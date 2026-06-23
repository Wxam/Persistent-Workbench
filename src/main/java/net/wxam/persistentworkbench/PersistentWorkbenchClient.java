package net.wxam.persistentworkbench;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.wxam.persistentworkbench.block.ModBlocks;
import net.wxam.persistentworkbench.menu.ModMenus;
import net.wxam.persistentworkbench.screen.WorkbenchRenderer;
import net.wxam.persistentworkbench.screen.WorkbenchScreen;

@Mod(value = PersistentWorkbench.MOD_ID, dist = Dist.CLIENT)
public class PersistentWorkbenchClient {

    public PersistentWorkbenchClient(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::registerScreens);
        modEventBus.addListener(this::registerRenderers);
    }

    private void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.WORKBENCH_MENU.get(), WorkbenchScreen::new);
    }

    private void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlocks.WORKBENCH_BE.get(), WorkbenchRenderer::new);
    }
}