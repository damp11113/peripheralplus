package dev.dpsoftware.peripheralplus.common.minecraft.blockEntitys;

import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface PeripheralBlockEntity {
    @Nullable
    IPeripheral getPeripheral(@NotNull Direction side);
}
