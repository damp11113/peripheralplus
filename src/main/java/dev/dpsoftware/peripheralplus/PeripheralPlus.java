package dev.dpsoftware.peripheralplus;

import com.mojang.logging.LogUtils;
import dev.dpsoftware.peripheralplus.common.minecraft.blocks.ModBlocks;
import dev.dpsoftware.peripheralplus.common.minecraft.blockEntitys.TileEntity.ModTileEntities;
import dev.dpsoftware.peripheralplus.common.CreativeTab;
import dev.dpsoftware.peripheralplus.common.minecraft.items.ModItems;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(PeripheralPlus.MODID)
public class PeripheralPlus {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "peripheralplus";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "peripheralplus" namespace

    public PeripheralPlus() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        CreativeTab.register(modEventBus);

        ModItems.register(modEventBus);

        ModBlocks.register(modEventBus);

        ModTileEntities.register(modEventBus);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModItems.TEST);
        }
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("Hello World from Peripheral+");
    }
}
