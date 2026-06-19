package net.wxam.persistentworkbench.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class WorkbenchBlockEntity extends BlockEntity implements Container {

    private static final int GRID_SIZE = 9;
    private final ItemStack[] items = new ItemStack[GRID_SIZE];

    public WorkbenchBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlocks.WORKBENCH_BE.get(), pos, blockState);
        for (int i = 0; i < GRID_SIZE; i++) {
            items[i] = ItemStack.EMPTY;
        }
    }

    @Override
    public int getContainerSize() {
        return GRID_SIZE;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return items[slot];
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack result = items[slot].split(amount);
        if (items[slot].isEmpty()) items[slot] = ItemStack.EMPTY;
        setChanged();
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack stack = items[slot];
        items[slot] = ItemStack.EMPTY;
        return stack;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        items[slot] = stack;
        setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        for (int i = 0; i < GRID_SIZE; i++) {
            items[i] = ItemStack.EMPTY;
        }
        setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        for (int i = 0; i < GRID_SIZE; i++) {
            if (!items[i].isEmpty()) {
                tag.put("slot_" + i, items[i].save(registries));
            }
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        for (int i = 0; i < GRID_SIZE; i++) {
            if (tag.contains("slot_" + i)) {
                items[i] = ItemStack.parseOptional(registries, tag.getCompound("slot_" + i));
            } else {
                items[i]= ItemStack.EMPTY;
            }
        }
    }
}
