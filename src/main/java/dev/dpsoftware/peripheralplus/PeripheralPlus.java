package dev.dpsoftware.peripheralplus;

import com.mojang.logging.LogUtils;
import dev.dpsoftware.peripheralplus.common.CreativeTab;
import dev.dpsoftware.peripheralplus.common.minecraft.blockEntitys.TileEntity.ModTileEntities;
import dev.dpsoftware.peripheralplus.common.minecraft.items.ModItems;
import dev.dpsoftware.peripheralplus.common.minecraft.blocks.ModBlocks;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(PeripheralPlus.MODID)
public class PeripheralPlus {

    public static final String MODID = "peripheralplus";
    public static final Logger LOGGER = LogUtils.getLogger();

    private static HitResult lastHit = null;
    private static int tickCounter = 0;

    public PeripheralPlus() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        CreativeTab.register(modEventBus);

        ModItems.register(modEventBus);

        ModBlocks.register(modEventBus);

        ModTileEntities.register(modEventBus);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {

        }
    }
}
