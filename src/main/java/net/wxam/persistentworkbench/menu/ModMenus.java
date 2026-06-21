package net.wxam.persistentworkbench.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.wxam.persistentworkbench.PersistentWorkbench;
import net.wxam.persistentworkbench.block.WorkbenchBlockEntity;

public class ModMenus {

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, PersistentWorkbench.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<WorkbenchMenu>> WORKBENCH_MENU =
            MENUS.register("workbench", () ->
                    IMenuTypeExtension.create((containerId, inventory, data) -> {
                        BlockPos pos = data.readBlockPos();
                        WorkbenchBlockEntity be = (WorkbenchBlockEntity) inventory.player.level().getBlockEntity(pos);
                        return new WorkbenchMenu(containerId, inventory, be, pos);
                    }));
}
