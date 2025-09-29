package dev.dpsoftware.peripheralplus.common.computercraft.peripherals;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import dev.dpsoftware.peripheralplus.common.computercraft.ModPeripheral;
import dev.dpsoftware.peripheralplus.common.minecraft.blockEntitys.TileEntity.LidarSensorTileEntity;
import dev.dpsoftware.peripheralplus.Quat;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class LidarSensorPeripheral extends ModPeripheral<LidarSensorTileEntity> {

    private BlockPos current_block_pos;
    private int range = 16;
    private int mode = 0;
    private int angleStep = 10;
    private int multiline = 1;
    private boolean use_custom_pos = false;

    private Quat rotation = new Quat(0, 0, 0, 0);

    public LidarSensorPeripheral(@Nullable LidarSensorTileEntity blockEntity) {
        super("lidar_sensor", blockEntity);
    }

    @Override
    @Nonnull
    public String getType() {
        return "lidar_sensor";
    }

    @LuaFunction
    public final void set_pos(int x, int y, int z) {
        current_block_pos = new BlockPos(x, y, z);
    }

    @LuaFunction
    public final void set_range(int r) {
        range = r;
    }

    @LuaFunction
    public final void set_mode(int m) {
        mode = m;
    }

    @LuaFunction
    public final void set_angle_step(int as) {
        angleStep = as;
    }

    @LuaFunction
    public final void set_multiline(int ml) {
        multiline = ml;
    }

    @LuaFunction
    public final void set_use_custom_pos(boolean ucp) {
        use_custom_pos = ucp;
    }

    @LuaFunction
    public final void set_rotation(int x, int y, int z, int w, int multiple) {
        rotation = new Quat((float) x/multiple, (float) y/multiple, (float) z/multiple, (float) w/multiple);
    }

    @LuaFunction
    public final Map<String, Object> scan() throws LuaException {
        LidarSensorTileEntity be = getTarget();
        if (be != null) {
            BlockPos scanPos = use_custom_pos && current_block_pos != null ? current_block_pos : be.getBlockPos();

            List<LidarSensorTileEntity.DetectionResult> results = be.getLidarScan360(be.getLevel(), scanPos, be.getBlockPos(), range, mode, angleStep, multiline, rotation);
            return convertResultsToLuaTable(results);
        }
        return new HashMap<>();
    }

    /**
     * Convert detection results to a Lua-compatible table format.
     */
    private Map<String, Object> convertResultsToLuaTable(List<LidarSensorTileEntity.DetectionResult> results) {
        Map<String, Object> luaTable = new HashMap<>();
        List<Map<String, Object>> detections = new ArrayList<>();

        for (int i = 0; i < results.size(); i++) {
            LidarSensorTileEntity.DetectionResult result = results.get(i);
            Map<String, Object> detection = new HashMap<>();

            detection.put("distance", result.distance);
            detection.put("line", result.yLine);
            detection.put("type", result.objectType.toLowerCase());
            detection.put("name", result.objectName);
            detection.put("angle", result.angle);

            detections.add(detection);
        }

        luaTable.put("count", results.size());
        luaTable.put("detections", detections);

        return luaTable;
    }

    @Override
    public boolean equals(@Nullable IPeripheral other) {
        return other == this && other.getType().equals(getType()) && other.getTarget() == this.getTarget();
    }
}