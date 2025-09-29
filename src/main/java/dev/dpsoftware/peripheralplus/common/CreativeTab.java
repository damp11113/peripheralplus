package dev.dpsoftware.peripheralplus.common;

import dev.dpsoftware.peripheralplus.common.minecraft.items.ModItems;
import dev.dpsoftware.peripheralplus.PeripheralPlus;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class CreativeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, PeripheralPlus.MODID);

    public static final RegistryObject<CreativeModeTab> PERIPHERALPLUS_TAB = CREATIVE_MODE_TAB.register("peripheralplus_tab",
            () -> CreativeModeTab.builder()
                    //.icon(()) // https://youtu.be/o6Xbp2dTEGA?si=STGBu9fWemGf7zQC&t=914
                    .title(Component.translatable("creativetab.peripheralplus_tab"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(ModItems.TEST.get());
                        pOutput.accept(ModItems.DISTANCE_SENSOR.get());
                        pOutput.accept(ModItems.LIDAR_SENSOR.get());
                        pOutput.accept(ModItems.IMU_SENSOR.get());
                    })
                    .build()
    );

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
