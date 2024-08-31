package dev.dpsoftware.peripheralplus.common.computercraft;

import dan200.computercraft.api.peripheral.IPeripheralProvider;
import dan200.computercraft.api.peripheral.IPeripheral;
import dev.dpsoftware.peripheralplus.common.minecraft.blockEntitys.PeripheralBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;

public class ModPeripheralProvider implements IPeripheralProvider {

    @Override
    public LazyOptional<IPeripheral> getPeripheral(Level level, BlockPos pos, Direction side) {
        if (level.getBlockEntity(pos) instanceof PeripheralBlockEntity peripheralBlockEntity) {
            IPeripheral peripheral = peripheralBlockEntity.getPeripheral(side);
            if (peripheral != null) {
                return LazyOptional.of(() -> peripheral);
            }
        }
        return LazyOptional.empty();
    }
}