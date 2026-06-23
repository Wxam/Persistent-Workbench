package net.wxam.persistentworkbench.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.resources.ResourceLocation;
import net.wxam.persistentworkbench.PersistentWorkbench;
import net.wxam.persistentworkbench.menu.ModMenus;
import net.wxam.persistentworkbench.menu.WorkbenchMenu;
import net.wxam.persistentworkbench.screen.WorkbenchScreen;

@JeiPlugin
public class JEIPlugin implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(PersistentWorkbench.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(
                WorkbenchScreen.class,
                148, 30, 16, 16,
                mezz.jei.api.constants.RecipeTypes.CRAFTING
        );
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(
                WorkbenchMenu.class,
                ModMenus.WORKBENCH_MENU.get(),
                mezz.jei.api.constants.RecipeTypes.CRAFTING,
                1, 9,
                10, 36
        );
    }
}