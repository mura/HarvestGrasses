package jp.stoic.minecraft.harvestgrasses;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@Mod(modid = HarvestGrassesMod.MODID, name = HarvestGrassesMod.NAME, version = HarvestGrassesMod.VERSION)
public class HarvestGrassesMod {
    public static final String MODID = "harvestgrassesmod";
    public static final String NAME = "Harvest Grasses Mod";
    public static final String VERSION = "0.1";

    private static Logger logger;
    private final static List<String> noDropBlockNames = Arrays.asList(
            "air", "flowing_water", "water", "flowing_lava", "lava", "fire", "barrier"
    );

    private Block grass;
    private Block tallGrass;
    private Method getBlock;

    private List<Block> blockList = Collections.emptyList();

    public HarvestGrassesMod() {
        for (Method method : IBlockState.class.getMethods()) {
            if (!method.getReturnType().equals(Block.class)) {
                continue;
            }
            getBlock = method;
            break;
        }
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        loadBlockList();
        if (!blockList.isEmpty() && getBlock != null) {
            grass = ForgeRegistries.BLOCKS.getValue(new ResourceLocation("grass"));
            tallGrass = ForgeRegistries.BLOCKS.getValue(new ResourceLocation("tallgrass"));

            MinecraftForge.EVENT_BUS.register(this);
            logger.info("It's Harvest Time!");
        }
    }

    private void loadBlockList() {
        Set<Block> noDropBlocks = noDropBlockNames.stream()
                .map(ResourceLocation::new)
                .filter(ForgeRegistries.BLOCKS::containsKey)
                .map(ForgeRegistries.BLOCKS::getValue)
                .collect(Collectors.toSet());
        blockList = ForgeRegistries.BLOCKS.getValuesCollection().stream()
                .filter(block -> !noDropBlocks.contains(block))
                .collect(Collectors.toList());
    }

    @SubscribeEvent
    public void harvestItem(BlockEvent.HarvestDropsEvent event) {
        final Block brokenItem;
        try {
            brokenItem = (Block) getBlock.invoke(event.getState());
        } catch (IllegalAccessException | InvocationTargetException e) {
            logger.info(e);
            return;
        }
        if (!grass.equals(brokenItem) && !tallGrass.equals(brokenItem)) {
            return;
        }
        if (event.getDrops().isEmpty()) {
            return;
        }

        final Random rand = new Random();
        final Block replaceBlock = blockList.get(rand.nextInt(blockList.size()));

        event.getDrops().clear();
        event.getDrops().add(new ItemStack(replaceBlock));
    }
}
