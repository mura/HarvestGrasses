package jp.stoic.minecraft.harvestgrasses;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(HarvestGrassesMod.MODID)
public class HarvestGrassesMod {
    static final String MODID = "harvestgrassesmod";
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    private final static List<String> noDropBlockNames = Arrays.asList(
            "air", "water", "lava", "fire", "barrier"
    );

    private final static List<String> grassNames = Arrays.asList(
            "grass", "tall_grass"
    );

    private Method getBlock;

    private List<Block> blockList = Collections.emptyList();
    private Set<Block> grassSet = Collections.emptySet();

    public HarvestGrassesMod() {
        for (Method method : IBlockState.class.getMethods()) {
            if (!method.getReturnType().equals(Block.class)) {
                continue;
            }
            getBlock = method;
            break;
        }

        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        loadBlockList();
        if (!blockList.isEmpty() && getBlock != null) {
            grassSet = grassNames.stream()
                    .map(ResourceLocation::new)
                    .filter(ForgeRegistries.BLOCKS::containsKey)
                    .map(ForgeRegistries.BLOCKS::getValue)
                    .collect(Collectors.toSet());

            // Register ourselves for server and other game events we are interested in
            MinecraftForge.EVENT_BUS.register(this);
            LOGGER.info("It's Harvest Time!");
        }
    }

    private void loadBlockList() {
        Set<Block> noDropBlocks = noDropBlockNames.stream()
                .map(ResourceLocation::new)
                .filter(ForgeRegistries.BLOCKS::containsKey)
                .map(ForgeRegistries.BLOCKS::getValue)
                .collect(Collectors.toSet());
        blockList = ForgeRegistries.BLOCKS.getValues().stream()
                .filter(block -> !noDropBlocks.contains(block))
                .collect(Collectors.toList());
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onHarvestDrops(BlockEvent.HarvestDropsEvent event) {
        LOGGER.info("onHarvestDrops: " + event);
        final Block brokenItem;
        try {
            brokenItem = (Block) getBlock.invoke(event.getState());
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOGGER.info(e);
            return;
        }
        LOGGER.info("onHarvestDrops: " + brokenItem);
        if (!grassSet.contains(brokenItem)) {
            return;
        }
        LOGGER.info("onHarvestDrops: " + event.getDrops());
        if (event.getDrops().isEmpty()) {
            return;
        }

        final Random rand = new Random();
        final Block replaceBlock = blockList.get(rand.nextInt(blockList.size()));

        event.getDrops().clear();
        event.getDrops().add(new ItemStack(replaceBlock));
    }
}
