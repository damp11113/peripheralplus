package dev.dpsoftware.peripheralplus.common.computercraft.peripherals;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import dev.dpsoftware.peripheralplus.common.computercraft.ModPeripheral;
import dev.dpsoftware.peripheralplus.common.minecraft.blockEntitys.TileEntity.IMUSensorTileEntity;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class IMUSensorPeripheral extends ModPeripheral<IMUSensorTileEntity> {

    public IMUSensorPeripheral(@Nullable IMUSensorTileEntity blockEntity) {
        super("imu_sensor", blockEntity);
    }

    @Override
    @Nonnull
    public String getType() {
        return "imu_sensor";
    }

    /**
     * Get the current distance value from the sensor.
     *
     * @return The distance value measured by the sensor.
     */
    @LuaFunction
    public final int scan() throws LuaException {
        IMUSensorTileEntity be = getTarget();
        if (be != null) {
            return -1;
        }
        return -1;
    }

    @Override
    public boolean equals(@Nullable IPeripheral other) {
        return other == this && other.getType().equals(getType()) && other.getTarget() == this.getTarget();
    }
}