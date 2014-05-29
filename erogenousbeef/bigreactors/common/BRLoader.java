package erogenousbeef.bigreactors.common;

import java.util.Arrays;

import li.cil.oc.api.Items;
import welfare93.bigreactors.packet.MainPacket;
import welfare93.bigreactors.packet.PacketHandler;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import erogenousbeef.core.multiblock.MultiblockEventHandler;


@Mod(modid = BigReactors.CHANNEL, name = BigReactors.NAME, version = BRConfig.VERSION, acceptedMinecraftVersions = BRConfig.MINECRAFT_VERSION)

public class BRLoader {

	public static final String MOD_ID = "BigReactors";
	public static final PacketHandler packethandler=new PacketHandler();
	@Instance(MOD_ID)
	public static BRLoader instance;

	@SidedProxy(clientSide = "erogenousbeef.bigreactors.client.ClientProxy", serverSide = "erogenousbeef.bigreactors.common.CommonProxy")
	public static CommonProxy proxy;
	
	@Mod.Metadata(MOD_ID)
	public static ModMetadata metadata;
	
	private MultiblockEventHandler multiblockEventHandler;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		BigReactors.registerOres(0, true);
		BigReactors.registerIngots(0);
		BigReactors.registerFuelRods(0, true);
		BigReactors.registerReactorPartBlocks(0, true);
		BigReactors.registerTurbineParts();
		BigReactors.registerSmallMachines(0,  true);
		BigReactors.registerFluids(0,  true);
		BigReactors.registerCreativeParts(0, true);

		BigReactors.eventHandler = new BREventHandler();
		MinecraftForge.EVENT_BUS.register(BigReactors.eventHandler);
		MinecraftForge.EVENT_BUS.register(proxy);
		
		multiblockEventHandler = new MultiblockEventHandler();
		MinecraftForge.EVENT_BUS.register(multiblockEventHandler);
		
		proxy.preInit();
		
		metadata.modId = MOD_ID;
		metadata.name  = BigReactors.NAME;
		metadata.description = "Adds large, multiblock power generation machines.  Need power? Go Big.";
		metadata.url = "http://www.big-reactors.com";
		metadata.version = BRConfig.VERSION;
		metadata.authorList = Arrays.asList(new String[] { "ErogenousBeef" });
		metadata.autogenerated = false;
		
		Fluid waterFluid = FluidRegistry.WATER; // Force-load water to prevent startup crashes
	}

	@EventHandler
	public void load(FMLInitializationEvent evt)
	{
		proxy.init();
		BigReactors.register(this);
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent evt) {
		proxy.postInit();
		packethandler.initialise();
		packethandler.registerPacket(MainPacket.class);
	        packethandler.postInitialise();
	}
	
	// GAME EVENT HANDLERS
	// FORGE EVENT HANDLERS

	// Handle bucketing of reactor fluids
	@SubscribeEvent 
    public void onBucketFill(FillBucketEvent e)
    {
            if(e.current.getItem() != net.minecraft.init.Items.bucket)
            {
                    return;
            }
            ItemStack filledBucket = fillBucket(e.world, e.target);
            if(filledBucket != null)
            {
                    e.world.setBlockToAir(e.target.blockX, e.target.blockY, e.target.blockZ);
                    e.result = filledBucket;
                    e.setResult(cpw.mods.fml.common.eventhandler.Event.Result.ALLOW);
            }
    }
    
    private ItemStack fillBucket(World world, MovingObjectPosition block)
    {
            Block blockd = world.getBlock(block.blockX, block.blockY, block.blockZ);
            if(blockd == BigReactors.fluidCyaniteStill) return new ItemStack(BigReactors.fluidCyaniteBucketItem);
            else if(blockd == BigReactors.fluidYelloriumStill) return new ItemStack(BigReactors.fluidYelloriumBucketItem);
            else return null;
    }
}
