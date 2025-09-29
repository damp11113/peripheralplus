package dev.dpsoftware.peripheralplus.common.minecraft.blockEntitys.TileEntity;

import dan200.computercraft.api.peripheral.IPeripheral;
import dev.dpsoftware.peripheralplus.Vec3;
import dev.dpsoftware.peripheralplus.common.minecraft.blockEntitys.PeripheralBlockEntity;
import dev.dpsoftware.peripheralplus.Quat;
import dev.dpsoftware.peripheralplus.common.computercraft.peripherals.LidarSensorPeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LidarSensorTileEntity extends BlockEntity implements PeripheralBlockEntity {
    private LidarSensorPeripheral peripheral;

    public LidarSensorTileEntity(BlockPos pos, BlockState state) {
        super(ModTileEntities.LIDAR_SENSOR.get(), pos, state);
    }

    public static class DetectionResult {
        public final int distance;
        public final String objectType;
        public final String objectName;
        public final int yLine; // Added to track which Y level this detection is from

        public final int angle;

        public DetectionResult(int distance, String objectType, String objectName, int yLine, int angle) {
            this.distance = distance;
            this.objectType = objectType;
            this.objectName = objectName;
            this.yLine = yLine;
            this.angle = angle;
        }
    }

    private String getEntityDisplayName(Entity entity) {
        return entity.getDisplayName().getString();
    }

    private String getEntityType(Entity entity) {
        if (entity instanceof Player) {
            return "Player";
        } else if (entity instanceof Mob) {
            return "Mob";
        } else {
            return "Entity";
        }
    }

    public List<DetectionResult> getLidarScan360(Level world, BlockPos pos, BlockPos pos2e, int range, int mode, int angleStep, int multiline, Quat rotation) {
        List<DetectionResult> results = new ArrayList<>();

        // Normalize the quaternion to ensure proper rotation
        Quat normalizedRotation = rotation.normalize();

        // Scan in multiple horizontal directions based on angle step
        for (int angle = 0; angle < 360; angle += angleStep) {
            double radians = Math.toRadians(angle);

            // Create initial direction vector (unit vector in XZ plane)
            Vec3 direction = new Vec3(Math.cos(radians), 0, Math.sin(radians));

            // Apply quaternion rotation to the direction
            Vec3 rotatedDirection = normalizedRotation.rotate(direction);

            // Calculate the actual output angle from the rotated direction
            int outputAngle = calculateAngleFromDirection(rotatedDirection);

            List<DetectionResult> directionResults = scanCustomDirection(world, pos, pos2e, rotatedDirection, range, mode, outputAngle, multiline, normalizedRotation);
            results.addAll(directionResults);
        }

        return results;
    }

    private List<DetectionResult> scanCustomDirection(Level world, BlockPos pos, BlockPos pos2e, Vec3 direction, int range, int mode, int angle, int multiline, Quat rotation) {
        List<DetectionResult> results = new ArrayList<>();
        List<Integer> yOffsets = calculateYOffsets(multiline);

        // Track which Y levels have already found an object
        Set<Integer> foundAtYLevel = new HashSet<>();
        BlockPos.MutableBlockPos checkPos = pos.mutable();
        BlockPos.MutableBlockPos checkPos2e = pos2e.mutable();

        for (int i = 1; i <= range; i += 1) {
            // If we've found objects at all Y levels, we can stop scanning this angle
            if (foundAtYLevel.size() == yOffsets.size()) {
                break;
            }

            // Scan all Y levels for this distance
            for (int yOffset : yOffsets) {
                // Skip this Y level if we already found something
                if (foundAtYLevel.contains(yOffset)) {
                    continue;
                }

                // Calculate position using rotated direction vector
                int blockX = pos.getX() + (int) Math.round(direction.x * i);
                int blockY = pos.getY() + yOffset + (int) Math.round(direction.y * i);
                int blockZ = pos.getZ() + (int) Math.round(direction.z * i);

                int blockX2e = pos2e.getX() + (int) Math.round(direction.x * i);
                int blockY2e = pos2e.getY() + yOffset + (int) Math.round(direction.y * i);
                int blockZ2e = pos2e.getZ() + (int) Math.round(direction.z * i);

                checkPos.set(blockX, blockY, blockZ);
                checkPos2e.set(blockX2e, blockY2e, blockZ2e);
                BlockState blockState = world.getBlockState(checkPos);

                // Check for blocks (modes 0 and 1)
                if ((mode == 0 || mode == 1) && !blockState.isAir()) {
                    String blockName = blockState.getBlock().getName().getString();
                    DetectionResult result = new DetectionResult(
                            i,
                            "Block",
                            blockName,
                            yOffset,
                            angle
                    );
                    results.add(result);
                    foundAtYLevel.add(yOffset);
                    continue;
                }

                // Check for entities (modes 0, 2, 3, 4, 5)
                if (mode != 1) {
                    AABB boundingBox = new AABB(checkPos2e).inflate(0.1);
                    List<Entity> entities = world.getEntitiesOfClass(Entity.class, boundingBox);

                    for (Entity entity : entities) {
                        String entityName = getEntityDisplayName(entity);
                        String entityType = getEntityType(entity);
                        DetectionResult result = null;

                        switch (mode) {
                            case 0: // All entities + blocks
                                result = new DetectionResult(i, entityType, entityName, yOffset, angle);
                                break;

                            case 2: // Any entity except player
                                if (!(entity instanceof Player)) {
                                    result = new DetectionResult(i, entityType, entityName, yOffset, angle);
                                }
                                break;

                            case 3: // Only player
                                if (entity instanceof Player) {
                                    result = new DetectionResult(i, "Player", entityName, yOffset, angle);
                                }
                                break;

                            case 4: // Only mobs
                                if (entity instanceof Mob) {
                                    result = new DetectionResult(i, "Mob", entityName, yOffset, angle);
                                }
                                break;

                            case 5: // Any entity including player
                                result = new DetectionResult(i, entityType, entityName, yOffset, angle);
                                break;
                        }

                        if (result != null) {
                            results.add(result);
                            foundAtYLevel.add(yOffset);
                            break;
                        }
                    }
                }
            }
        }

        return results;
    }

    // Calculate angle from direction vector (for proper output angle)
    private static int calculateAngleFromDirection(Vec3 direction) {
        // Calculate angle in degrees from the direction vector
        double angle = Math.toDegrees(Math.atan2(direction.z, direction.x));

        // Normalize to 0-360 range
        int result = (int) Math.round(angle);
        if (result < 0) {
            result += 360;
        }
        return result % 360;
    }

    /**
     * Calculate Y offsets based on multiline parameter
     * @param multiline Number of lines to scan
     * @return List of Y offsets to scan
     */
    private List<Integer> calculateYOffsets(int multiline) {
        List<Integer> offsets = new ArrayList<>();

        if (multiline <= 1) {
            // Default case: only scan current Y level
            offsets.add(0);
        } else if (multiline == 2) {
            // Scan Y-1 and Y+1 (skip Y+0)
            offsets.add(-1);
            offsets.add(1);
        } else {
            // For multiline >= 3, calculate symmetric range
            int halfRange = multiline / 2;

            for (int i = -halfRange; i <= halfRange; i++) {
                offsets.add(i);
            }
        }

        return offsets;
    }

    public IPeripheral getPeripheral(@NotNull Direction side) {
        if (peripheral == null)
            peripheral = new LidarSensorPeripheral(this);
        return peripheral;
    }
}
