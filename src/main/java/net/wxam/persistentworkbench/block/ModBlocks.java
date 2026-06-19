package net.wxam.persistentworkbench.block;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.wxam.persistentworkbench.PersistentWorkbench;

public class ModBlocks {

    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(PersistentWorkbench.MOD_ID);

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, PersistentWorkbench.MOD_ID);

    public static final DeferredBlock<WorkbenchBlock> WORKBENCH =
            BLOCKS.register("workbench", () -> new WorkbenchBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CRAFTING_TABLE)));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WorkbenchBlockEntity>> WORKBENCH_BE =
            BLOCK_ENTITIES.register("workbench", () -> BlockEntityType.Builder.of(WorkbenchBlockEntity::new, WORKBENCH.get()).build(null));
}
