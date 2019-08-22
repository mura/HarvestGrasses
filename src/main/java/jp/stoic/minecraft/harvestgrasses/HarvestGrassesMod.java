package jp.stoic.minecraft.harvestgrasses;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(HarvestGrassesMod.MODID)
public class HarvestGrassesMod {
    static final String MODID = "harvestgrassesmod";
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public HarvestGrassesMod() {
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        ForgeRegistries.ITEMS.getValues().stream()
                .filter(item -> !Items.AIR.equals(item))
                .forEach(item -> MinecraftForge.addGrassSeed(new ItemStack(item), 10));
        LOGGER.info("It's Harvest Time!");
    }
}
