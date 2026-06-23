package net.wxam.persistentworkbench.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.wxam.persistentworkbench.block.WorkbenchBlockEntity;

public class LinkCrystalWandItem extends Item {

    public LinkCrystalWandItem() {
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

            context.getLevel().playSound(null, pos,
                    net.minecraft.sounds.SoundEvents.AMETHYST_BLOCK_CHIME,
                    net.minecraft.sounds.SoundSource.BLOCKS, 1.0f, 1.0f);
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

        if (context.getLevel() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    net.minecraft.core.particles.ParticleTypes.ENCHANT,
                    pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                    20, 0.5, 0.5, 0.5, 0.1
            );
            serverLevel.sendParticles(
                    net.minecraft.core.particles.ParticleTypes.ENCHANT,
                    boundPos.getX() + 0.5, boundPos.getY() + 1.0, boundPos.getZ() + 0.5,
                    20, 0.5, 0.5, 0.5, 0.1
            );
        }

        context.getLevel().playSound(null, pos,
                net.minecraft.sounds.SoundEvents.BEACON_ACTIVATE,
                net.minecraft.sounds.SoundSource.BLOCKS, 1.0f, 1.0f);
        context.getPlayer().displayClientMessage(
                Component.translatable("item.persistentworkbench.link_crystal.linked"), true);
        return InteractionResult.CONSUME;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.has(DataComponents.CUSTOM_DATA);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!player.isShiftKeyDown()) return InteractionResultHolder.pass(stack);

        if (stack.has(DataComponents.CUSTOM_DATA)) {
            if (!level.isClientSide) {
                stack.remove(DataComponents.CUSTOM_DATA);
                player.displayClientMessage(
                        Component.translatable("item.persistentworkbench.link_crystal_wand.reset"), true);
            }
            return InteractionResultHolder.consume(stack);
        }

        return InteractionResultHolder.pass(stack);
    }
}