package bspkrs.crystalwing;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import bspkrs.util.CommonUtils;

public class ItemCrystalWing extends Item
{
    public ItemCrystalWing(int i)
    {
        super(i);
        maxStackSize = 1;
        this.setCreativeTab(CreativeTabs.tabTransport);
        
        if (CWSettings.uses > 0)
        {
            setMaxDamage(CWSettings.uses - 1);
        }
    }
    
    @Override
    public Item setUnlocalizedName(String par1Str)
    {
        super.setUnlocalizedName(par1Str);
        this.setTextureName(par1Str.replaceAll("\\.", ":"));
        return this;
    }
    
    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer)
    {
        if (!world.isRemote)
        {
            if (entityPlayer.dimension == -1)
            {
                itemStack = null;
                world.playSoundAtEntity(entityPlayer, "fire.ignite", 1.0F, 1.0F);
                itemStack = new ItemStack(CWSettings.crystalWingBurning, 1);
                return itemStack;
            }
            else if (world.provider.dimensionId > 0)
                return itemStack;
            
            ChunkCoordinates chunkCoords = entityPlayer.getBedLocation(world.provider.dimensionId);
            
            if (chunkCoords == null)
                chunkCoords = world.getSpawnPoint();
            
            chunkCoords = CWSettings.verifyRespawnCoordinates(world, chunkCoords, false);
            
            if (chunkCoords == null)
                chunkCoords = world.getSpawnPoint();
            
            if (CWSettings.isForgeVersion)
                entityPlayer.addChatMessage(StatCollector.translateToLocal("crystalwing.teleporthome.chatmessage"));
            else
                entityPlayer.addChatMessage("Magical winds brought you home");
            
            entityPlayer.rotationPitch = 0.0F;
            entityPlayer.rotationYaw = 0.0F;
            entityPlayer.setPositionAndUpdate(chunkCoords.posX + 0.5D, chunkCoords.posY + 0.1D, chunkCoords.posZ);
            
            while (!world.getCollidingBoundingBoxes(entityPlayer, entityPlayer.boundingBox).isEmpty())
            {
                entityPlayer.setPositionAndUpdate(entityPlayer.posX, entityPlayer.posY + 1.0D, entityPlayer.posZ);
            }
            
            world.playSoundAtEntity(entityPlayer, "mob.endermen.portal", 1.0F, 1.0F);
            CommonUtils.spawnExplosionParticleAtEntity(entityPlayer);
            
            if (CWSettings.uses > 0)
                itemStack.damageItem(1, entityPlayer);
        }
        
        return itemStack;
    }
}
