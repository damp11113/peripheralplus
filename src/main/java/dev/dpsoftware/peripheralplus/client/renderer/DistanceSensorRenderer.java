package dev.dpsoftware.peripheralplus.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.dpsoftware.peripheralplus.common.minecraft.blockEntitys.TileEntity.DistanceSensorTileEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;

public class DistanceSensorRenderer implements BlockEntityRenderer<DistanceSensorTileEntity> {
    public DistanceSensorRenderer(Context context) {
        // Renderer initialization if needed
    }

    @Override
    public void render(DistanceSensorTileEntity tileEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        // Implement rendering logic
    }
}
