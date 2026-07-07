package net.wxam.persistentworkbench.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.wxam.persistentworkbench.PersistentWorkbench;

public class WorkbenchBlock extends BaseEntityBlock {

    public static final MapCodec<WorkbenchBlock> CODEC = simpleCodec(WorkbenchBlock::new);

    public WorkbenchBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WorkbenchBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hitResult) {
        if (level.isClientSide) return InteractionResult.SUCCESS;

        PersistentWorkbench.LOGGER.info("Opening workbench at {}", pos);

        if (level.getBlockEntity(pos) instanceof WorkbenchBlockEntity workbench) {
            PersistentWorkbench.LOGGER.info("BlockEntity found, opening menu");
            player.openMenu(workbench.getMenuProvider(), buf -> buf.writeBlockPos(pos));
        } else {
            PersistentWorkbench.LOGGER.info("BlockEntity NOT found!");
        }

        return InteractionResult.CONSUME;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos,
                         BlockState newState, boolean movedByPiston) {
        if (state.getBlock() != newState.getBlock()) {
            if (level.getBlockEntity(pos) instanceof WorkbenchBlockEntity workbench) {
                if (workbench.isLinked() && workbench.getLinkedPos() != null) {
                    if (level.getBlockEntity(workbench.getLinkedPos()) instanceof WorkbenchBlockEntity other) {
                        other.setLinked(false, null);
                        other.clearContent();
                        level.playSound(null, workbench.getLinkedPos(),
                                net.minecraft.sounds.SoundEvents.BEACON_DEACTIVATE,
                                net.minecraft.sounds.SoundSource.BLOCKS, 1.0f, 1.0f);
                    }
                }

                for (int i = 0; i < workbench.getContainerSize(); i++) {
                    ItemStack stack = workbench.getItem(i);
                    if (!stack.isEmpty()) {
                        popResource(level, pos, stack);
                    }
                }
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}