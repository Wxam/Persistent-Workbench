package net.wxam.persistentworkbench.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.wxam.persistentworkbench.block.WorkbenchBlockEntity;

public class WorkbenchMenu extends CraftingMenu {

    private final WorkbenchBlockEntity blockEntity;

    public WorkbenchMenu(int containerId, Inventory playerInventory, WorkbenchBlockEntity blockEntity, BlockPos pos) {
        super(containerId, playerInventory, ContainerLevelAccess.NULL);
        this.blockEntity = blockEntity;

        for (int i = 0; i < 9; i++) {
            this.getSlot(i + 1).set(blockEntity.getItem(i).copy());
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                final int index = col + row * 3;
                final Slot original = this.getSlot(index + 1);
                this.slots.set(index + 1, new Slot(original.container, original.getContainerSlot(), original.x, original.y) {
                    @Override
                    public void setChanged() {
                        super.setChanged();
                        ItemStack current = this.getItem();
                        ItemStack stored = blockEntity.getItem(index);
                        if (!ItemStack.matches(current, stored)) {
                            blockEntity.setItem(index, current.copy());
                        }
                    }
                });
            }
        }

        blockEntity.setLinkedMenu(this);
    }

    public void syncFromBlockEntity() {
        for (int i = 0; i < 9; i++) {
            ItemStack beItem = blockEntity.getItem(i);
            ItemStack slotItem = this.getSlot(i + 1).getItem();
            if (!ItemStack.matches(beItem, slotItem)) {
                this.getSlot(i + 1).set(beItem.copy());
            }
        }
        this.slotsChanged(this.getSlot(1).container);
    }

    public WorkbenchBlockEntity getBlockEntity() {
        return blockEntity;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
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