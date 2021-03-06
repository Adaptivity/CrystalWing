package bspkrs.crystalwing;

import static net.minecraftforge.common.ChestGenHooks.BONUS_CHEST;
import static net.minecraftforge.common.ChestGenHooks.DUNGEON_CHEST;
import static net.minecraftforge.common.ChestGenHooks.PYRAMID_DESERT_CHEST;
import static net.minecraftforge.common.ChestGenHooks.PYRAMID_JUNGLE_CHEST;
import static net.minecraftforge.common.ChestGenHooks.STRONGHOLD_LIBRARY;
import static net.minecraftforge.common.ChestGenHooks.VILLAGE_BLACKSMITH;

import java.io.File;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.ChestGenHooks;
import bspkrs.helpers.block.BlockHelper;
import bspkrs.helpers.world.WorldHelper;
import bspkrs.util.CommonUtils;
import bspkrs.util.Const;
import bspkrs.util.config.Configuration;
import cpw.mods.fml.common.registry.GameRegistry;

public final class CWSettings
{
    public final static String   VERSION_NUMBER           = Const.MCVERSION + ".r02";
    
    private final static boolean allowDebugLoggingDefault = false;
    public static boolean        allowDebugLogging        = allowDebugLoggingDefault;
    private final static int     usesDefault              = 8;
    public static int            uses                     = usesDefault;
    private final static int     teleDistanceDefault      = 500;
    public static int            teleDistance             = teleDistanceDefault;
    
    public static Item           crystalWing;
    public static Item           crystalWingBurning;
    public static Item           crystalWingBurned;
    public static Item           enderScepter;
    public static Achievement    burnedWing;
    
    public static Configuration  config;
    
    public static Configuration getConfig()
    {
        return config;
    }
    
    public static void loadConfig(File file)
    {
        if (!CommonUtils.isObfuscatedEnv())
        { // debug settings for deobfuscated execution
          //            if (file.exists())
          //                file.delete();
        }
        
        config = new Configuration(file);
        
        syncConfig();
    }
    
    public static void syncConfig()
    {
        String ctgyGen = Configuration.CATEGORY_GENERAL;
        
        config.load();
        
        config.addCustomCategoryComment(ctgyGen, "ATTENTION: Editing this file manually is no longer necessary. \n" +
                "On the Mods list screen select the entry for CrystalWing, then click the Config button to modify these settings.");
        
        allowDebugLogging = config.getBoolean(ConfigElement.ALLOW_DEBUG_LOGGING.key(), ctgyGen, allowDebugLoggingDefault, ConfigElement.ALLOW_DEBUG_LOGGING.desc(), ConfigElement.ALLOW_DEBUG_LOGGING.languageKey());
        uses = config.getInt(ConfigElement.USES.key(), ctgyGen, usesDefault, 0, 5280, ConfigElement.USES.desc(), ConfigElement.USES.languageKey());
        teleDistance = config.getInt(ConfigElement.TELE_DISTANCE.key(), ctgyGen, teleDistanceDefault, 100, 50000, ConfigElement.TELE_DISTANCE.desc(), ConfigElement.TELE_DISTANCE.languageKey());
        
        config.save();
    }
    
    public static void registerStuff()
    {
        crystalWing = (new ItemCrystalWing()).setUnlocalizedName("crystalwing.crystalWing");
        crystalWingBurning = (new ItemCrystalWingBurning()).setUnlocalizedName("crystalwing.crystalWingBurning");
        crystalWingBurned = (new ItemCrystalWingBurned(teleDistance)).setUnlocalizedName("crystalwing.crystalWingBurned");
        enderScepter = (new ItemEnderScepter()).setUnlocalizedName("crystalwing.enderScepter");
        
        GameRegistry.registerItem(crystalWing, "crystalwing.crystalWing", "CrystalWing");
        GameRegistry.registerItem(crystalWingBurning, "crystalwing.crystalWingBurning", "CrystalWing");
        GameRegistry.registerItem(crystalWingBurned, "crystalwing.crystalWingBurned", "CrystalWing");
        GameRegistry.registerItem(enderScepter, "crystalwing.enderScepter", "CrystalWing");
        
        GameRegistry.addRecipe(new ItemStack(crystalWing, 1), new Object[] {
                "GGG", "EFF", Character.valueOf('G'), Items.gold_ingot, Character.valueOf('E'), Items.ender_pearl, Character.valueOf('F'), Items.feather
        });
        
        burnedWing = (new Achievement("burnedWing", "burnedWing", 9, -5, crystalWingBurning, null)).initIndependentStat().registerStat();
        
        ChestGenHooks.addItem(PYRAMID_DESERT_CHEST, new WeightedRandomChestContent(new ItemStack(crystalWing, 1), 1, 1, 3));
        ChestGenHooks.addItem(PYRAMID_DESERT_CHEST, new WeightedRandomChestContent(new ItemStack(crystalWingBurned, 1), 1, 1, 2));
        ChestGenHooks.addItem(PYRAMID_JUNGLE_CHEST, new WeightedRandomChestContent(new ItemStack(crystalWing, 1), 1, 1, 2));
        ChestGenHooks.addItem(STRONGHOLD_LIBRARY, new WeightedRandomChestContent(new ItemStack(crystalWing, 1), 1, 1, 2));
        ChestGenHooks.addItem(VILLAGE_BLACKSMITH, new WeightedRandomChestContent(new ItemStack(crystalWing, 1), 1, 1, 2));
        ChestGenHooks.addItem(BONUS_CHEST, new WeightedRandomChestContent(new ItemStack(crystalWing, 1), 1, 1, 2));
        ChestGenHooks.addItem(DUNGEON_CHEST, new WeightedRandomChestContent(new ItemStack(crystalWing, 1), 1, 1, 3));
        ChestGenHooks.addItem(DUNGEON_CHEST, new WeightedRandomChestContent(new ItemStack(crystalWingBurned, 1), 1, 1, 2));
    }
    
    /**
     * Ensure that a block enabling respawning exists at the specified coordinates and find an empty space nearby to spawn.
     */
    public static ChunkCoordinates verifyRespawnCoordinates(World world, ChunkCoordinates chunkCoords, boolean par2)
    {
        if (!world.isRemote)
        {
            IChunkProvider ichunkprovider = world.getChunkProvider();
            ichunkprovider.loadChunk(chunkCoords.posX - 3 >> 4, chunkCoords.posZ - 3 >> 4);
            ichunkprovider.loadChunk(chunkCoords.posX + 3 >> 4, chunkCoords.posZ - 3 >> 4);
            ichunkprovider.loadChunk(chunkCoords.posX - 3 >> 4, chunkCoords.posZ + 3 >> 4);
            ichunkprovider.loadChunk(chunkCoords.posX + 3 >> 4, chunkCoords.posZ + 3 >> 4);
        }
        
        ChunkCoordinates c = chunkCoords;
        Block block = WorldHelper.getBlock(world, c.posX, c.posY, c.posZ);
        
        if (block.equals(Blocks.bed))
        {
            return block.getBedSpawnPosition(world, c.posX, c.posY, c.posZ, null);
        }
        else
        {
            Material material = BlockHelper.getBlockMaterial(WorldHelper.getBlock(world, chunkCoords.posX, chunkCoords.posY, chunkCoords.posZ));
            Material material1 = BlockHelper.getBlockMaterial(WorldHelper.getBlock(world, chunkCoords.posX, chunkCoords.posY + 1, chunkCoords.posZ));
            boolean flag1 = !material.isSolid() && !material.isLiquid();
            boolean flag2 = !material1.isSolid() && !material1.isLiquid();
            return par2 && flag1 && flag2 ? chunkCoords : null;
        }
    }
}
