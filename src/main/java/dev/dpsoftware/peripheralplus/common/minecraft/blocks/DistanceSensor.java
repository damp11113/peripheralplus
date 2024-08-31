package dev.dpsoftware.peripheralplus.common.minecraft.blocks;

import dev.dpsoftware.peripheralplus.common.minecraft.blockEntitys.TileEntity.DistanceSensorTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;

public class DistanceSensor extends Block implements EntityBlock {
    public DistanceSensor(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(BlockStateProperties.FACING, Direction.NORTH)); // Set default facing
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DistanceSensorTileEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.FACING);
    }

    // This method sets the block's facing direction based on player placement, sneaking status, and vertical pitch angle
    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Player player = context.getPlayer();
        if (player != null) {
            Direction facing;

            // Get the player's pitch (looking angle)
            float pitch = player.getXRot();

            // If the player is looking up or down, set the block to face up or down
            if (pitch > 65) {
                facing = Direction.DOWN;
            } else if (pitch < -65) {
                facing = Direction.UP;
            } else {
                // Otherwise, face the block in the player's horizontal direction
                facing = player.getDirection();

                // Invert direction if the player is sneaking
                if (player.isShiftKeyDown()) {
                    facing = facing.getOpposite();
                }
            }

            // Set the block's facing direction
            return this.defaultBlockState().setValue(BlockStateProperties.FACING, facing);
        }
        return this.defaultBlockState();
    }

    @Override
    public BlockState rotate(BlockState state, LevelAccessor level, BlockPos pos, Rotation direction) {
        return state.setValue(BlockStateProperties.FACING, direction.rotate(state.getValue(BlockStateProperties.FACING)));
    }
}
