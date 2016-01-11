package org.tasgo.coherence;

import java.io.File;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tasgo.coherence.client.ClientEventHandler;
import org.tasgo.coherence.client.PostCohere;
import org.tasgo.coherence.server.Server;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Mod(modid = Coherence.MODID, version = Coherence.VERSION, acceptableRemoteVersions = "*")
public class Coherence
{
	@Instance("Coherence")
	public static Coherence instance;
	
    public static final String MODID = "Coherence";
    public static final String VERSION = "1.7b02";
    public static boolean connectOnStart;

	public boolean debug;
    
    public boolean postCohered = false;
    public static File configFile;
    public static String modsToKeep;
    
    private static final Logger logger = LogManager.getLogger("Coherence");
    /*public static final int activationTicks = 60; //atm, no point
    private int ticks = 0;
    private boolean connected = true;*/
    
    /**Previous connected address*/
    public String address;
    public static int port;
    
    //=========================================CLIENT SIDE CODE=================================================================
    @EventHandler
    @SideOnly(Side.CLIENT) 
    public void preInit(FMLPreInitializationEvent event) throws IOException {
    	
    	configFile = event.getSuggestedConfigurationFile();
    	Configuration config = new Configuration(configFile);
    	config.load();
    	
    	Property connectProperty = config.get(Configuration.CATEGORY_GENERAL, "connectOnStart", false);
    	connectProperty.comment = "Set this to true if you want to connect back to the server after cohering is done and Minecraft loads again."
    			+ "\nThis is not quite ready yet, so enable this at your own risk.";
    	connectOnStart = connectProperty.getBoolean();
    	
    	Property addressProperty = config.get(Configuration.CATEGORY_GENERAL, "connectToServer", "null");
    	addressProperty.comment = "This tells Coherence what server to connect to on start if connectOnStart is true, and is also for persistence."
    			+ "\nDon't touch this, unless you want to break a lot of things.";
    	address = addressProperty.getString(); addressProperty.set("null");
    	
    	Property debugProperty = config.get(Configuration.CATEGORY_GENERAL, "debug", false);
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
    	logger.info("Registering event handler");
		FMLCommonHandler.instance().bus().register(new ClientEventHandler());
	}
    //=========================================END CLIENT SIDE CODE=================================================================
    
    //=========================================SERVER SIDE CODE=====================================================================
    @EventHandler
    @SideOnly(Side.SERVER)
    public void postInit(FMLPostInitializationEvent event) throws IOException
    {
    	new Server();
    }
    //=========================================END SERVER SIDE CODE=================================================================
}
