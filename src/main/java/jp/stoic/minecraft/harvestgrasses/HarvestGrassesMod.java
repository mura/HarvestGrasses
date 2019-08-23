package jp.stoic.minecraft.harvestgrasses;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(HarvestGrassesMod.MODID)
public class HarvestGrassesMod {
    static final String MODID = "harvestgrassesmod";
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();
    private final static List<String> noDropItemNames = Arrays.asList(
            "air", "barrier"
    );

    public HarvestGrassesMod() {
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        IForgeRegistry<Item> itemRegistry = GameRegistry.findRegistry(Item.class);
        Set<Item> noDropItems = noDropItemNames.stream()
                .map(ResourceLocation::new)
                .filter(itemRegistry::containsKey)
                .map(itemRegistry::getValue)
                .collect(Collectors.toSet());
        itemRegistry.getValues().stream()
                .filter(item -> !noDropItems.contains(item))
                .forEach(this::addGrassSeed);
        LOGGER.info("It's Harvest Time!");
    }

    private void addGrassSeed(Item item) {
        MinecraftForge.addGrassSeed(new ItemStack(item), 10);
    }
}
