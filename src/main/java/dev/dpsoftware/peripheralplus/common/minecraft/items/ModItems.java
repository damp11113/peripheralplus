package dev.dpsoftware.peripheralplus.common.minecraft.items;

import dev.dpsoftware.peripheralplus.PeripheralPlus;
import dev.dpsoftware.peripheralplus.common.minecraft.blocks.ModBlocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, PeripheralPlus.MODID);

    public static final RegistryObject<Item> DISTANCE_SENSOR = ITEMS.register("distance_sensor",
            () -> new BlockItem(ModBlocks.DISTANCE_SENSOR.get(), new Item.Properties()));

    public static final RegistryObject<Item> TEST = ITEMS.register("test", () -> new Item(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
