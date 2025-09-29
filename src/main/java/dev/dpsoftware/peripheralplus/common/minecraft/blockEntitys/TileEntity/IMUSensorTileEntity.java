package dev.dpsoftware.peripheralplus.common.minecraft.blockEntitys.TileEntity;

import dan200.computercraft.api.peripheral.IPeripheral;
import dev.dpsoftware.peripheralplus.common.computercraft.peripherals.IMUSensorPeripheral;
import dev.dpsoftware.peripheralplus.common.minecraft.blockEntitys.PeripheralBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class IMUSensorTileEntity extends BlockEntity implements PeripheralBlockEntity {
    private IMUSensorPeripheral peripheral;

    public IMUSensorTileEntity(BlockPos pos, BlockState state) {
        super(ModTileEntities.IMU_SENSOR.get(), pos, state);
    }

    public static class IMUOut  {
        public float x;
        public float y;
        public float z;
    }

    public IPeripheral getPeripheral(@NotNull Direction side) {
        if (peripheral == null)
            peripheral = new IMUSensorPeripheral(this);
        return peripheral;
    }
}
