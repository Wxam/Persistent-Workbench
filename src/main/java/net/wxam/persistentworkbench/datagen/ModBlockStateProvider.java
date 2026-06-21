package net.wxam.persistentworkbench.datagen;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.wxam.persistentworkbench.PersistentWorkbench;
import net.wxam.persistentworkbench.block.ModBlocks;

public class ModBlockStateProvider extends BlockStateProvider {

    public ModBlockStateProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, PersistentWorkbench.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlockWithItem(ModBlocks.WORKBENCH.get(),
                models().getExistingFile(modLoc("block/workbench")));
    }
}
