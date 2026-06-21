package net.wxam.persistentworkbench.item;

import net.minecraft.world.item.BlockItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.wxam.persistentworkbench.PersistentWorkbench;
import net.wxam.persistentworkbench.block.ModBlocks;

public class ModItems {

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(PersistentWorkbench.MOD_ID);

    public static final DeferredItem<BlockItem> WORKBENCH =
            ITEMS.registerSimpleBlockItem("workbench", ModBlocks.WORKBENCH);

    public static final DeferredItem<LinkCrystalItem> LINK_CRYTAL =
            ITEMS.register("link_crystal", LinkCrystalItem::new);
}
