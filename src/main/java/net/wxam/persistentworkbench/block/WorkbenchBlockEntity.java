package net.wxam.persistentworkbench.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.wxam.persistentworkbench.menu.WorkbenchMenu;
import org.jetbrains.annotations.Nullable;

public class WorkbenchBlockEntity extends BlockEntity implements Container {

    private static final int GRID_SIZE = 9;
    private final ItemStack[] items = new ItemStack[GRID_SIZE];

    private boolean linked = false;
    private BlockPos linkedPos = null;
    private WorkbenchMenu linkedMenu = null;

    public WorkbenchBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlocks.WORKBENCH_BE.get(), pos, blockState);
        for (int i = 0; i < GRID_SIZE; i++) {
            items[i] = ItemStack.EMPTY;
        }
    }

    public void directSetItem(int slot, ItemStack stack) {
        items[slot] = stack;
        setChanged();
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

    public void setLinkedMenu(WorkbenchMenu menu) {
        this.linkedMenu = menu;
        if (menu != null) {
            menu.syncFromBlockEntity();
        }
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        items[slot] = stack;
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
        if (linkedMenu != null) {
            linkedMenu.syncFromBlockEntity();
        }
        if (linked && linkedPos != null && level != null) {
            if (level.getBlockEntity(linkedPos) instanceof WorkbenchBlockEntity other) {
                other.items[slot] = stack.copy();
                other.setChanged();
                if (other.level != null && !other.level.isClientSide) {
                    other.level.sendBlockUpdated(other.worldPosition, other.getBlockState(), other.getBlockState(), 3);
                }
                if (other.linkedMenu != null) {
                    other.linkedMenu.syncFromBlockEntity();
                }
            }
        }
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
        tag.putBoolean("linked", linked);
        if (linkedPos != null) {
            tag.putLong("linkedPos", linkedPos.asLong());
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        for (int i = 0; i < GRID_SIZE; i++) {
            if (tag.contains("slot_" + i)) {
                items[i] = ItemStack.parseOptional(registries, tag.getCompound("slot_" + i));
            } else {
                items[i] = ItemStack.EMPTY;
            }
        }
        linked = tag.getBoolean("linked");
        if (tag.contains("linkedPos")) {
            linkedPos = BlockPos.of(tag.getLong("linkedPos"));
        }
    }

    public MenuProvider getMenuProvider() {
        return new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return linked
                        ? Component.translatable("block.persistentworkbench.workbench_linked")
                        : Component.translatable("block.persistentworkbench.workbench");
            }

            @Override
            public @Nullable AbstractContainerMenu createMenu(int containerId, net.minecraft.world.entity.player.Inventory inventory, Player player) {
                return new WorkbenchMenu(containerId, inventory, WorkbenchBlockEntity.this, worldPosition);
            }
        };
    }

    public boolean isLinked() {
        return linked;
    }

    public void setLinked(boolean linked, BlockPos linkedPos) {
        this.linked = linked;
        this.linkedPos = linkedPos;
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public BlockPos getLinkedPos() {
        return linkedPos;
    }

    @Override
    public net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket getUpdatePacket() {
        return net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }
}