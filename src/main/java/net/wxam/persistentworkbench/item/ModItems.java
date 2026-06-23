package net.wxam.persistentworkbench.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.wxam.persistentworkbench.PersistentWorkbench;
import net.wxam.persistentworkbench.block.ModBlocks;

public class ModItems {

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(PersistentWorkbench.MOD_ID);

    public static final DeferredItem<BlockItem> WORKBENCH =
            ITEMS.registerSimpleBlockItem("workbench", ModBlocks.WORKBENCH);

    public static final DeferredItem<LinkCrystalWandItem> LINK_CRYSTAL_WAND =
            ITEMS.register("link_crystal_wand", LinkCrystalWandItem::new);

    public static final DeferredItem<Item> LINK_CRYSTAL =
            ITEMS.registerSimpleItem("link_crystal");
}
