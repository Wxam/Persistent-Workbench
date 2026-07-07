package net.wxam.persistentworkbench.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.wxam.persistentworkbench.PersistentWorkbench;
import net.wxam.persistentworkbench.block.WorkbenchBlockEntity;

public class WorkbenchMenu extends CraftingMenu {

    private final WorkbenchBlockEntity blockEntity;
    private boolean isSyncing = false;

    public WorkbenchMenu(int containerId, Inventory playerInventory, WorkbenchBlockEntity blockEntity, BlockPos pos) {
        super(containerId, playerInventory, ContainerLevelAccess.NULL);
        this.blockEntity = blockEntity;

        isSyncing = true;
        for (int i = 0; i < 9; i++) {
            this.getSlot(i + 1).set(blockEntity.getItem(i).copy());
        }
        isSyncing = false;

        blockEntity.setLinkedMenu(this);
        super.slotsChanged(this.getSlot(1).container);
    }

    public void syncFromBlockEntity() {
        if (isSyncing) return;
        isSyncing = true;
        try {
            for (int i = 0; i < 9; i++) {
                ItemStack beItem = blockEntity.getItem(i);
                ItemStack slotItem = this.getSlot(i + 1).getItem();
                if (!ItemStack.matches(beItem, slotItem)) {
                    this.getSlot(i + 1).set(beItem.copy());
                }
            }
            super.slotsChanged(this.getSlot(1).container);
        } finally {
            isSyncing = false;
        }
    }

    @Override
    public void slotsChanged(net.minecraft.world.Container container) {
        if (isSyncing) return;
        isSyncing = true;
        try {
            for (int i = 0; i < 9; i++) {
                blockEntity.setItem(i, this.getSlot(i + 1).getItem().copy());
            }
            if (blockEntity.getLevel() != null && !blockEntity.getLevel().isClientSide) {
                net.minecraft.world.item.crafting.CraftingInput input = net.minecraft.world.item.crafting.CraftingInput.of(3, 3,
                        java.util.List.of(
                                this.getSlot(1).getItem(), this.getSlot(2).getItem(), this.getSlot(3).getItem(),
                                this.getSlot(4).getItem(), this.getSlot(5).getItem(), this.getSlot(6).getItem(),
                                this.getSlot(7).getItem(), this.getSlot(8).getItem(), this.getSlot(9).getItem()
                        ));
                blockEntity.getLevel().getRecipeManager()
                        .getRecipeFor(net.minecraft.world.item.crafting.RecipeType.CRAFTING, input, blockEntity.getLevel())
                        .ifPresentOrElse(
                                recipe -> this.getSlot(0).set(recipe.value().assemble(input, blockEntity.getLevel().registryAccess())),
                                () -> this.getSlot(0).set(net.minecraft.world.item.ItemStack.EMPTY)
                        );
            }
            super.slotsChanged(container);
        } finally {
            isSyncing = false;
        }
    }

    @Override
    public void removed(Player player) {
        for (int i = 0; i < 9; i++) {
            PersistentWorkbench.LOGGER.info("Slot {} before save: {}", i, this.getSlot(i + 1).getItem());
            blockEntity.directSetItem(i, this.getSlot(i + 1).getItem().copy());
            PersistentWorkbench.LOGGER.info("BlockEntity slot {} after save: {}", i, blockEntity.getItem(i));
        }
        blockEntity.setLinkedMenu(null);
    }

    @Override
    public MenuType<?> getType() {
        return ModMenus.WORKBENCH_MENU.get();
    }

    @Override
    public boolean stillValid(Player player) {
        return blockEntity.stillValid(player);
    }
}