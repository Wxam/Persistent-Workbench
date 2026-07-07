package net.wxam.persistentworkbench.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.wxam.persistentworkbench.PersistentWorkbench;
import net.wxam.persistentworkbench.menu.WorkbenchMenu;

public class WorkbenchScreen extends AbstractContainerScreen<WorkbenchMenu> {

    private static final ResourceLocation TEXTURE = ResourceLocation.withDefaultNamespace(
            "textures/gui/container/crafting_table.png");

    public WorkbenchScreen(WorkbenchMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
        net.minecraft.client.Minecraft.getInstance().execute(() ->
                PersistentWorkbench.LOGGER.info("WorkbenchScreen constructor called!"));
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        guiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}