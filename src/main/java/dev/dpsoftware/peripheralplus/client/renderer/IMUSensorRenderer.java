package dev.dpsoftware.peripheralplus.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.dpsoftware.peripheralplus.common.minecraft.blockEntitys.TileEntity.IMUSensorTileEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;

public class IMUSensorRenderer implements BlockEntityRenderer<IMUSensorTileEntity> {
    // The constructor must remain the same for the provider to call it
    public IMUSensorRenderer(Context context) {
        // ...
    }

    @Override
    public void render(IMUSensorTileEntity tileEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        // Implement rendering logic
    }
}