package dev.dpsoftware.peripheralplus.common.minecraft.blockEntitys.TileEntity;

import dev.dpsoftware.peripheralplus.PeripheralPlus;
import dev.dpsoftware.peripheralplus.common.minecraft.blocks.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModTileEntities {
    public static final DeferredRegister<BlockEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, PeripheralPlus.MODID);

    public static final RegistryObject<BlockEntityType<DistanceSensorTileEntity>> DISTANCE_SENSOR = TILE_ENTITIES.register("distance_sensor",
            () -> BlockEntityType.Builder.of(DistanceSensorTileEntity::new, ModBlocks.DISTANCE_SENSOR.get()).build(null));

    public static final RegistryObject<BlockEntityType<LidarSensorTileEntity>> LIDAR_SENSOR = TILE_ENTITIES.register("lidar_sensor",
            () -> BlockEntityType.Builder.of(LidarSensorTileEntity::new, ModBlocks.LIDAR_SENSOR.get()).build(null));


    public static final RegistryObject<BlockEntityType<IMUSensorTileEntity>> IMU_SENSOR = TILE_ENTITIES.register("imu_sensor",
            () -> BlockEntityType.Builder.of(IMUSensorTileEntity::new, ModBlocks.IMU_SENSOR.get()).build(null));

    public static void register(IEventBus eventBus) {
        TILE_ENTITIES.register(eventBus);
    }
}
