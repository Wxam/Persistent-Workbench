package net.wxam.persistentworkbench.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.wxam.persistentworkbench.block.WorkbenchBlockEntity;

import java.util.HashMap;
import java.util.Map;

public class WorkbenchRenderer implements BlockEntityRenderer<WorkbenchBlockEntity> {

    private final ItemRenderer itemRenderer;
    private static final Map<BlockPos, Float> currentYaws = new HashMap<>();

    public WorkbenchRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = Minecraft.getInstance().getItemRenderer();
    }

    @Override
    public void render(WorkbenchBlockEntity blockEntity, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

        boolean hasItems = false;
        for (int i = 0; i < 9; i++) {
            if (!blockEntity.getItem(i).isEmpty()) {
                hasItems = true;
                break;
            }
        }
        if (!hasItems) return;

        float time = (blockEntity.getLevel().getGameTime() + partialTick) / 20f;

        BlockPos pos = blockEntity.getBlockPos();
        net.minecraft.world.entity.player.Player player = Minecraft.getInstance().player;

        double dx = player.getX() - (pos.getX() + 0.5);
        double dz = player.getZ() - (pos.getZ() + 0.5);
        float targetYaw = (float) Math.toDegrees(Math.atan2(dx, dz));
        float snappedYaw = Math.round(targetYaw / 90f) * 90f;

        float currentYaw = currentYaws.getOrDefault(pos, snappedYaw);

        float diff = snappedYaw - currentYaw;
        while (diff > 180) diff -= 360;
        while (diff < -180) diff += 360;

        currentYaw += diff * 0.1f;
        currentYaws.put(pos, currentYaw);

        poseStack.pushPose();
        poseStack.translate(0.5f, 1.13f, 0.5f);
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(currentYaw));

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                ItemStack stack = blockEntity.getItem(col + row * 3);
                if (stack.isEmpty()) continue;

                poseStack.pushPose();

                float x = -0.25f + col * 0.25f;
                float z = -0.25f + row * 0.25f;
                float yOffset = (float) Math.sin(time * 2 + col + row) * 0.02f;

                poseStack.translate(x, yOffset, z);
                poseStack.scale(0.2f, 0.2f, 0.2f);

                Minecraft.getInstance().getItemRenderer().renderStatic(
                        stack,
                        ItemDisplayContext.FIXED,
                        packedLight,
                        packedOverlay,
                        poseStack,
                        bufferSource,
                        blockEntity.getLevel(),
                        0
                );

                poseStack.popPose();
            }
        }

        poseStack.popPose();
    }
}