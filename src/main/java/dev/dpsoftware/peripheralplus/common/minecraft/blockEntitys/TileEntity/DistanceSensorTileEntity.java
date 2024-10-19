package dev.dpsoftware.peripheralplus.common.minecraft.blockEntitys.TileEntity;

import dan200.computercraft.api.peripheral.IPeripheral;
import dev.dpsoftware.peripheralplus.common.computercraft.peripherals.DistanceSensorPeripheral;
import dev.dpsoftware.peripheralplus.common.minecraft.blockEntitys.PeripheralBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;

public class DistanceSensorTileEntity extends BlockEntity implements PeripheralBlockEntity {
    private DistanceSensorPeripheral peripheral;

    public DistanceSensorTileEntity(BlockPos pos, BlockState state) {
        super(ModTileEntities.DISTANCE_SENSOR.get(), pos, state);
    }

    /**
     * Get distance to the closest block in front of the sensor within a range of 20 blocks.
     */
    public int getDistance(Level world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        Direction facing = state.getValue(BlockStateProperties.FACING);  // Get the block's facing direction
        BlockPos.MutableBlockPos checkPos = pos.mutable();

        for (int i = 1; i <= 20; i++) {
            checkPos.set(pos).move(facing, i);  // Move in the direction the sensor is facing
            BlockState blockState = world.getBlockState(checkPos);

            // Skip if it's air or any other "empty" block
            if (!blockState.isAir()) {
                return i;  // Return the distance to the detected block
            }
        }

        return -1;  // Return -1 if no block is detected within 20 blocks
    }

    public IPeripheral getPeripheral(@NotNull Direction side) {
        if (peripheral == null)
            peripheral = new DistanceSensorPeripheral(this);
        return peripheral;
    }
}
