package net.wxam.persistentworkbench.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.wxam.persistentworkbench.PersistentWorkbench;

@EventBusSubscriber(modid = PersistentWorkbench.MOD_ID)
public class ModDatagen {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();

        generator.addProvider(event.includeClient(), new ModBlockStateProvider(output, event.getExistingFileHelper()));
        generator.addProvider(event.includeClient(), new ModItemModelProvider(output, event.getExistingFileHelper()));
        generator.addProvider(event.includeServer(), new ModLootTableProvider(output, event.getLookupProvider()));
    }
}
