package org.tasgo.coherence;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tasgo.coherence.client.JavaCommandBuilder;
import org.tasgo.coherence.client.PostCohere;
import org.tasgo.coherence.server.Server;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import cpw.mods.fml.server.FMLServerHandler;

@Mod(modid = Coherence.MODID, version = Coherence.VERSION, acceptableRemoteVersions = "*")
public class Coherence
{
	@Instance("Coherence")
	public static Coherence instance;
	
    public static final String MODID = "Coherence";
    public static final String VERSION = "1.7b01";
    //public static final int activationTicks = 60;
    public static boolean connectOnStart, debug;
    
    public static boolean postCohered = false;
    public static File configFile;
    public static String modsToKeep;
    
    private static final Logger logger = LogManager.getLogger("Coherence");
    private int ticks = 0;
    private boolean connected = true;
    
    /**Previous connected address*/
    public static String address;
    public static int port;
    
    //=========================================CLIENT SIDE CODE=================================================================
    @EventHandler
    @SideOnly(Side.CLIENT) 
    public void preInit(FMLPreInitializationEvent event) throws IOException {
    	configFile = event.getSuggestedConfigurationFile();
    	Configuration config = new Configuration(configFile);
    	config.load();
    	
    	Property connectProperty = config.get(config.CATEGORY_GENERAL, "connectOnStart", false);
    	connectProperty.comment = "Set this to true if you want to connect back to the server after cohering is done and Minecraft loads again."
    			+ "\nThis is not quite ready yet, so enable this at your own risk.";
    	connectOnStart = connectProperty.getBoolean();
    	
    	Property addressProperty = config.get(config.CATEGORY_GENERAL, "connectToServer", "null");
    	addressProperty.comment = "This tells Coherence what server to connect to on start if connectOnStart is true, and is also for persistence."
    			+ "\nDon't touch this, unless you want to break a lot of things.";
    	address = addressProperty.getString(); addressProperty.set("null");
    	
    	Property debugProperty = config.get(config.CATEGORY_GENERAL, "debug", true);
    	debugProperty.comment = "This tells Coherence if it should turn on advanced debugging features."
    			+ "\nUsed mainly for testing purposes.";
    	debug = debugProperty.getBoolean();
    	
    	if (!address.equals("null")) {
    		new PostCohere();
    		postCohered = true;
    	}
    	logger.info("Previously cohered: " + postCohered + ". Previous address: " + address);
    	
    	config.save();
    	
    	try {
			PostCohere.detectCrash();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    @SideOnly(Side.CLIENT)
    @EventHandler
	public void init(FMLInitializationEvent event) {
		//FMLCommonHandler.instance().bus().register(this); //atm, no point
	}
    //=========================================END CLIENT SIDE CODE=================================================================
    
    //=========================================SERVER SIDE CODE=====================================================================
    @EventHandler
    @SideOnly(Side.SERVER)
    public void postInit(FMLPostInitializationEvent event) throws IOException
    {
    	Server server = new Server();
    }
    //=========================================END SERVER SIDE CODE=================================================================
}
