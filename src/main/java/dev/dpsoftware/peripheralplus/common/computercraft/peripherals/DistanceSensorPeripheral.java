package dev.dpsoftware.peripheralplus.common.computercraft.peripherals;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import dev.dpsoftware.peripheralplus.common.minecraft.blockEntitys.TileEntity.DistanceSensorTileEntity;
import dev.dpsoftware.peripheralplus.common.computercraft.ModPeripheral;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class DistanceSensorPeripheral extends ModPeripheral<DistanceSensorTileEntity> {

    public DistanceSensorPeripheral(@Nullable DistanceSensorTileEntity blockEntity) {
        super("distance_sensor", blockEntity);
    }

    @Override
    @Nonnull
    public String getType() {
        return "distance_sensor";
    }

    /**
     * Get the current distance value from the sensor.
     *
     * @return The distance value measured by the sensor.
     */
    @LuaFunction
    public final int scan() throws LuaException {
        DistanceSensorTileEntity be = getTarget();
        if (be != null) {
            return be.getDistance(be.getLevel(), be.getBlockPos());
        }
        return -1;
    }

    @Override
    public boolean equals(@Nullable IPeripheral other) {
        return other == this && other.getType().equals(getType()) && other.getTarget() == this.getTarget();
    }
}