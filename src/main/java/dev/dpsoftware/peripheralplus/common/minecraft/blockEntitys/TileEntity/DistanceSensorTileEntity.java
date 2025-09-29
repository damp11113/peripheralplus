package dev.dpsoftware.peripheralplus.common.minecraft.blockEntitys.TileEntity;

import dan200.computercraft.api.peripheral.IPeripheral;
import dev.dpsoftware.peripheralplus.common.computercraft.peripherals.DistanceSensorPeripheral;
import dev.dpsoftware.peripheralplus.common.minecraft.blockEntitys.PeripheralBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DistanceSensorTileEntity extends BlockEntity implements PeripheralBlockEntity {
    private DistanceSensorPeripheral peripheral;

    public DistanceSensorTileEntity(BlockPos pos, BlockState state) {
        super(ModTileEntities.DISTANCE_SENSOR.get(), pos, state);
    }

    public int getDistance(Level world, BlockPos pos, int range, int mode) {
        // mode
        // 0 = all (Any Entity (include player) + Block)
        // 1 = Only block
        // 2 = only any entity not player
        // 3 = only player
        // 4 = only mob
        // 5 = any entity include player

        BlockState state = world.getBlockState(pos);
        Direction facing = state.getValue(BlockStateProperties.FACING);
        BlockPos.MutableBlockPos checkPos = pos.mutable();

        for (int i = 1; i <= range; i++) {
            checkPos.set(pos).move(facing, i);
            BlockState blockState = world.getBlockState(checkPos);

            // Check for blocks (modes 0 and 1)
            if ((mode == 0 || mode == 1) && !blockState.isAir()) {
                return i;
            }

            // Check for entities (modes 0, 2, 3, 4, 5)
            if (mode != 1) {
                // Create a bounding box for the current position
                AABB boundingBox = new AABB(checkPos).inflate(0.1);
                List<Entity> entities = world.getEntitiesOfClass(Entity.class, boundingBox);

                for (Entity entity : entities) {
                    switch (mode) {
                        case 0: // All entities + blocks
                            return i;

                        case 2: // Any entity except player
                            if (!(entity instanceof Player)) {
                                return i;
                            }
                            break;

                        case 3: // Only player
                            if (entity instanceof Player) {
                                return i;
                            }
                            break;

                        case 4: // Only mobs
                            if (entity instanceof Mob) {
                                return i;
                            }
                            break;

                        case 5: // Any entity including player
                            return i;
                    }
                }
            }
        }

        return -1; // No detection within range
    }

    public IPeripheral getPeripheral(@NotNull Direction side) {
        if (peripheral == null)
            peripheral = new DistanceSensorPeripheral(this);
        return peripheral;
    }
}
