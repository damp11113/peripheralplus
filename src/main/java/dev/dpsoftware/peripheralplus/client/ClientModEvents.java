package dev.dpsoftware.peripheralplus.client;

import dan200.computercraft.api.ForgeComputerCraftAPI;
import dev.dpsoftware.peripheralplus.client.renderer.DistanceSensorRenderer;
import dev.dpsoftware.peripheralplus.common.computercraft.ModPeripheralProvider;
import dev.dpsoftware.peripheralplus.PeripheralPlus;
import dev.dpsoftware.peripheralplus.client.renderer.IMUSensorRenderer;
import dev.dpsoftware.peripheralplus.client.renderer.LidarSensorRenderer;
import dev.dpsoftware.peripheralplus.common.minecraft.blockEntitys.TileEntity.ModTileEntities;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = PeripheralPlus.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientModEvents {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        // Register TileEntityRenderer
        BlockEntityRenderers.register(ModTileEntities.DISTANCE_SENSOR.get(), DistanceSensorRenderer::new);
        BlockEntityRenderers.register(ModTileEntities.LIDAR_SENSOR.get(), LidarSensorRenderer::new);
        BlockEntityRenderers.register(ModTileEntities.IMU_SENSOR.get(), IMUSensorRenderer::new);

        ForgeComputerCraftAPI.registerPeripheralProvider(new ModPeripheralProvider());
    }
}
