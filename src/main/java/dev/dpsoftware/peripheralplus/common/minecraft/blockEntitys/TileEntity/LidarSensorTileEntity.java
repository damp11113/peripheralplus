package dev.dpsoftware.peripheralplus.common.minecraft.blockEntitys.TileEntity;

import dan200.computercraft.api.peripheral.IPeripheral;
import dev.dpsoftware.peripheralplus.PeripheralPlus;
import dev.dpsoftware.peripheralplus.ppVec3;
import dev.dpsoftware.peripheralplus.common.minecraft.blockEntitys.PeripheralBlockEntity;
import dev.dpsoftware.peripheralplus.ppQuat;
import dev.dpsoftware.peripheralplus.common.computercraft.peripherals.LidarSensorPeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaterniond;
import org.joml.Quaternionf;

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
        public final double distance;
        public final String objectType;
        public final String objectName;
        public final int yLine; // Added to track which Y level this detection is from

        public final int angle;

        public DetectionResult(double distance, String objectType, String objectName, int yLine, int angle) {
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

    public List<DetectionResult> getLidarScan360(Level world, BlockPos pos, BlockPos pos2e, int range, int mode, int angleStep, int multiline, ppQuat rotation) {
        List<DetectionResult> results = new ArrayList<>();

        // Normalize the quaternion to ensure proper rotation
        ppQuat normalizedRotation = rotation.normalize();
        BlockState state = world.getBlockState(pos2e);
        Direction facing = state.getValue(BlockStateProperties.FACING);

        // Create a quaternion for the facing direction
        ppQuat facingRotation = getFacingQuaternion(facing);

        // Combine facing rotation with the input rotation
        ppQuat combinedRotation = ppQuat.fromMCQuat(facingRotation.mcQuat().mul(normalizedRotation.mcQuat()));

        // Scan in multiple horizontal directions based on angle step
        for (int angle = 0; angle < 360; angle += angleStep) {
            double radians = Math.toRadians(angle);

            // Create initial direction vector (unit vector in XZ plane)
            ppVec3 direction = new ppVec3(Math.cos(radians), 0, Math.sin(radians));

            // Apply combined rotation (facing + custom rotation) to the direction
            ppVec3 rotatedDirection = combinedRotation.rotate(direction);

            // Calculate the actual output angle from the rotated direction
            int outputAngle = calculateAngleFromDirection(rotatedDirection);

            List<DetectionResult> directionResults = scanCustomDirection(world, pos, pos2e, rotatedDirection.mcVec3(), range, mode, angle, outputAngle, multiline, combinedRotation);
            results.addAll(directionResults);
        }

        return results;
    }

    // Helper method to convert Direction to Quaternion rotation
    private ppQuat getFacingQuaternion(Direction facing) {
        return switch (facing) {
            case NORTH -> ppQuat.fromXYZ(0, 0, 0, 1);  // No rotation (0°)
            case SOUTH -> ppQuat.fromXYZ(0, (float) Math.PI, 0, 1);  // 180° around Y
            case WEST -> ppQuat.fromXYZ(0, (float) Math.PI / 2, 0, 1);  // 90° around Y
            case EAST -> ppQuat.fromXYZ(0, (float) -Math.PI / 2, 0, 1);  // -90° around Y
            case UP -> ppQuat.fromXYZ((float) -Math.PI / 2, 0, 0, 1);  // -90° around X
            case DOWN -> ppQuat.fromXYZ((float) Math.PI / 2, 0, 0, 1);  // 90° around X
        };
    }

    private List<DetectionResult> scanCustomDirection(Level world, BlockPos pos, BlockPos pos2e, Vec3 direction, int range, int mode, int angle, int angle2, int multiline, ppQuat rotation) {
        List<DetectionResult> results = new ArrayList<>();
        List<Integer> yOffsets = calculateYOffsets(multiline);
        Set<Integer> foundAtYLevel = new HashSet<>();

        for (int yOffset : yOffsets) {
            if (foundAtYLevel.contains(yOffset)) continue;

            // Entity detection - still use AABB sweeping
            if (mode != 1 && !foundAtYLevel.contains(yOffset)) {
                DetectionResult entityResult = scanEntitiesAlongRay(world, pos2e, direction, range, mode, yOffset, angle2);
                if (entityResult != null) {
                    results.add(entityResult);
                    foundAtYLevel.add(yOffset);
                }
            }

            // Raycast for blocks
            if (mode == 0 || mode == 1) {
                Vec3 startPos = Vec3.atCenterOf(pos); // start at the block center (safer than corner)

                // push start position a little bit along the ray direction
                Vec3 safeStart = startPos.add(direction.normalize().scale(1.25));

                Vec3 endPos = startPos.add(direction.scale(range)).add(0, yOffset, 0);

                BlockHitResult hit = world.clip(new ClipContext(
                        safeStart,
                        endPos,
                        ClipContext.Block.VISUAL,
                        ClipContext.Fluid.NONE,
                        null
                ));

                if (hit.getType() == HitResult.Type.BLOCK) {
                    BlockState blockState = world.getBlockState(hit.getBlockPos());
                    if (!blockState.isAir()) {
                        double distance = startPos.distanceTo(hit.getLocation());
                        String blockName = blockState.getBlock().getName().getString();
                        results.add(new DetectionResult(distance, "Block", blockName, yOffset, angle));
                        foundAtYLevel.add(yOffset);

                        if (mode == 1) continue; // Block-only mode
                    }
                }

            }
        }

        return results;
    }

    private DetectionResult scanEntitiesAlongRay(Level world, BlockPos pos2e, Vec3 direction, int range, int mode, int yOffset, int angle) {
        // Use AABB sweep for entities (since clip() doesn't detect entities)
        for (int i = 1; i <= range; i++) {
            int blockX = pos2e.getX() + (int) Math.round(direction.x * i);
            int blockY = pos2e.getY() + yOffset + (int) Math.round(direction.y * i);
            int blockZ = pos2e.getZ() + (int) Math.round(direction.z * i);

            BlockPos.MutableBlockPos checkPos = new BlockPos(blockX, blockY, blockZ).mutable();
            AABB boundingBox = new AABB(checkPos).inflate(0.1);
            List<Entity> entities = world.getEntitiesOfClass(Entity.class, boundingBox);

            for (Entity entity : entities) {
                if (shouldDetectEntity(entity, mode)) {
                    String entityName = getEntityDisplayName(entity);
                    String entityType = getEntityType(entity);
                    return new DetectionResult(i, entityType, entityName, yOffset, angle);
                }
            }
        }
        return null;
    }

    private boolean shouldDetectEntity(Entity entity, int mode) {
        return switch (mode) {
            case 0 -> true; // All
            case 2 -> !(entity instanceof Player); // No player
            case 3 -> entity instanceof Player; // Only player
            case 4 -> entity instanceof Mob; // Only mobs
            case 5 -> true; // All entities
            default -> false;
        };
    }

    // Calculate angle from direction vector (for proper output angle)
    private static int calculateAngleFromDirection(ppVec3 direction) {
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
