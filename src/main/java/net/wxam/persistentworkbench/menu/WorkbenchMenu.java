package net.wxam.persistentworkbench.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.wxam.persistentworkbench.PersistentWorkbench;
import net.wxam.persistentworkbench.block.WorkbenchBlockEntity;

public class WorkbenchMenu extends AbstractContainerMenu {

    private final WorkbenchBlockEntity blockEntity;
    private final Level level;
    private final ResultContainer resultContainer = new ResultContainer();
    private final ContainerLevelAccess access;

    public WorkbenchMenu(int containerId, Inventory playerInventory, WorkbenchBlockEntity blockEntity, BlockPos pos) {
        super(ModMenus.WORKBENCH_MENU.get(), containerId);
        this.blockEntity = blockEntity;
        this.level = playerInventory.player.level();
        this.access = ContainerLevelAccess.create(level, pos);

        // Result slot (slot 0)
        this.addSlot(new Slot(resultContainer, 0, 124, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            @Override
            public void onTake(Player player, ItemStack stack) {
                consumeIngredients();
                super.onTake(player, stack);
            }
        });

        // Crafting grid (slots 1-9)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                this.addSlot(new Slot(blockEntity, col + row * 3, 30 + col * 18, 17 + row * 18));
            }
        }

        // Player inventory (slots 9-35)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        // Player hotbar (slots 36-44)
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }

        blockEntity.setLinkedMenu(this);
        updateResult();
    }

    private void consumeIngredients() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = blockEntity.getItem(i);
            if (!stack.isEmpty()) {
                stack.shrink(1);
                blockEntity.setItem(i, stack.isEmpty() ? ItemStack.EMPTY : stack);
            }
        }
    }

    @Override
    public void slotsChanged(Container container) {
        super.slotsChanged(container);
        updateResult();
    }

    private void updateResult() {
        if (level.isClientSide) return;

        CraftingInput input = CraftingInput.of(3, 3,
                java.util.List.of(
                        blockEntity.getItem(0), blockEntity.getItem(1), blockEntity.getItem(2),
                        blockEntity.getItem(3), blockEntity.getItem(4), blockEntity.getItem(5),
                        blockEntity.getItem(6), blockEntity.getItem(7), blockEntity.getItem(8)
                ));

        PersistentWorkbench.LOGGER.info("Grid: {}", input.items());

        level.getRecipeManager()
                .getRecipeFor(RecipeType.CRAFTING, input, level)
                .ifPresentOrElse(
                        recipe -> {
                            PersistentWorkbench.LOGGER.info("Recipe found: {}", recipe.value());
                            resultContainer.setItem(0, recipe.value().assemble(input, level.registryAccess()));
                        },
                        () -> {
                            PersistentWorkbench.LOGGER.info("No recipe found!");
                            resultContainer.setItem(0, ItemStack.EMPTY);
                        }
                );
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack returnStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            returnStack = slotStack.copy();

            if (index == 0) {
                if (!this.moveItemStackTo(slotStack, 10, 46, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(slotStack, returnStack);
            } else if (index >= 1 && index <= 9) {
                if (!this.moveItemStackTo(slotStack, 10, 46, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 10 && index <= 45) {
                if (!this.moveItemStackTo(slotStack, 1, 10, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (slotStack.getCount() == returnStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, slotStack);
        }

        return returnStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return blockEntity.stillValid(player);
    }

    public WorkbenchBlockEntity getBlockEntity() {
        return blockEntity;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        blockEntity.setLinkedMenu(null);
    }
}