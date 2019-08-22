package jp.stoic.minecraft.harvestgrasses;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.logging.log4j.Logger;

@Mod(modid = HarvestGrassesMod.MODID, name = HarvestGrassesMod.NAME, version = HarvestGrassesMod.VERSION)
public class HarvestGrassesMod {
    static final String MODID = "harvestgrassesmod";
    static final String NAME = "Harvest Grasses Mod";
    static final String VERSION = "0.3";

    private static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        ForgeRegistries.ITEMS.getValuesCollection().stream()
                .filter(item -> !Items.AIR.equals(item))
                .forEach(item -> MinecraftForge.addGrassSeed(new ItemStack(item), 10));
        logger.info("It's Harvest Time!");
    }
}
