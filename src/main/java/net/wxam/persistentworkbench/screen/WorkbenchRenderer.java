package net.wxam.persistentworkbench.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.wxam.persistentworkbench.block.WorkbenchBlockEntity;

public class WorkbenchRenderer implements BlockEntityRenderer<WorkbenchBlockEntity> {

    private final ItemRenderer itemRenderer;

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

        net.minecraft.client.Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        float cameraYaw = camera.getYRot();
        float snappedYaw = Math.round(cameraYaw / 90f) * 90f;

        poseStack.pushPose();
        poseStack.translate(0.5f, 1.13f, 0.5f);
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(-snappedYaw + 180f));

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