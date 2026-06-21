package net.wxam.persistentworkbench.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.wxam.persistentworkbench.block.WorkbenchBlockEntity;

public class LinkCrystalItem extends Item {

    public LinkCrystalItem() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getLevel().isClientSide) return InteractionResult.SUCCESS;

        BlockPos pos = context.getClickedPos();
        ItemStack stack = context.getItemInHand();

        if (!(context.getLevel().getBlockEntity(pos) instanceof WorkbenchBlockEntity target)) {
            return InteractionResult.PASS;
        }

        if (!stack.has(DataComponents.CUSTOM_DATA)) {
            if (target.isLinked()) {
                context.getPlayer().displayClientMessage(
                        Component.translatable("item.persistentworkbench.link_crystal.already_linked"), true);
                return InteractionResult.FAIL;
            }

            CompoundTag tag = new CompoundTag();
            tag.putLong("boundPos", pos.asLong());
            stack.set(DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.of(tag));

            context.getPlayer().displayClientMessage(
                    Component.translatable("item.persistentworkbench.link_crystal.bound"), true);
            return InteractionResult.CONSUME;
        }

        CompoundTag tag = stack.get(DataComponents.CUSTOM_DATA).copyTag();
        BlockPos boundPos = BlockPos.of(tag.getLong("boundPos"));

        if (boundPos.equals(pos)) {
            context.getPlayer().displayClientMessage(
                    Component.translatable("item.persistentworkbench.link_crystal.same_block"), true);
            return InteractionResult.FAIL;
        }

        if (!(context.getLevel().getBlockEntity(boundPos) instanceof WorkbenchBlockEntity source)) {
            context.getPlayer().displayClientMessage(
                    Component.translatable("item.persistentworkbench.link_crystal.lost"), true);
            stack.remove(DataComponents.CUSTOM_DATA);
            return InteractionResult.FAIL;
        }

        if (target.isLinked()) {
            context.getPlayer().displayClientMessage(
                    Component.translatable("item.persistentworkbench.link_crystal.already_linked"), true);
            return InteractionResult.FAIL;
        }

        if (source.isLinked()) {
            context.getPlayer().displayClientMessage(
                    Component.translatable("item.persistentworkbench.link_crystal.already_linked"), true);
            return InteractionResult.FAIL;
        }

        source.setLinked(true, pos);
        target.setLinked(true, boundPos);

        for (int i = 0; i < 9; i++) {
            target.setItem(i, source.getItem(i).copy());
        }

        stack.remove(DataComponents.CUSTOM_DATA);

        context.getPlayer().displayClientMessage(
                Component.translatable("item.persistentworkbench.link_crystal.linked"), true);

        return InteractionResult.CONSUME;
    }
}