package net.wxam.persistentworkbench.datagen;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.wxam.persistentworkbench.PersistentWorkbench;
import net.wxam.persistentworkbench.item.ModItems;

public class ModItemModelProvider extends ItemModelProvider {

    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, PersistentWorkbench.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        withExistingParent(ModItems.WORKBENCH.getId().getPath(),
                modLoc("block/workbench"));

        basicItem(ModItems.LINK_CRYSTAL_WAND.get());
        basicItem(ModItems.LINK_CRYSTAL.get());
    }
}
