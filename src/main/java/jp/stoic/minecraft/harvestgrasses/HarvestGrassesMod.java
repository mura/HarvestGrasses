package jp.stoic.minecraft.harvestgrasses;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mod(modid = HarvestGrassesMod.MODID, name = HarvestGrassesMod.NAME, version = HarvestGrassesMod.VERSION)
public class HarvestGrassesMod {
    static final String MODID = "harvestgrassesmod";
    static final String NAME = "Harvest Grasses Mod";
    static final String VERSION = "0.3";

    private static Logger logger;
    private final static List<String> noDropItemNames = Arrays.asList(
            "air", "barrier"
    );

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        IForgeRegistry<Item> itemRegistry = GameRegistry.findRegistry(Item.class);
        Set<Item> noDropItems = noDropItemNames.stream()
                .map(ResourceLocation::new)
                .filter(itemRegistry::containsKey)
                .map(itemRegistry::getValue)
                .collect(Collectors.toSet());
        itemRegistry.getValuesCollection().stream()
                .filter(item -> !noDropItems.contains(item))
                .forEach(this::addGrassSeed);
        logger.info("It's Harvest Time!");
    }

    private void addGrassSeed(Item item) {
        MinecraftForge.addGrassSeed(new ItemStack(item), 10);
    }
}
