package org.dezord.coherence;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dezord.coherence.client.PostCohere;
import org.dezord.coherence.server.Server;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
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
    public static final String VERSION = "1.7r02";
    public static final int activationTicks = 60;
    public static boolean connectOnStart;
    
    public static boolean postCohered = false;
    public static File configName;
    
    private static final Logger logger = LogManager.getLogger("Coherence");
    private int ticks = 0;
    private boolean connected = true;
    
    public static String address;
    public static int port;
    
    //=========================================CLIENT SIDE CODE=================================================================
    @EventHandler
    @SideOnly(Side.CLIENT)
    public void PreInit(FMLPreInitializationEvent event) {
    	configName = event.getSuggestedConfigurationFile();
    	Configuration config = new Configuration(configName);
    	config.load();
    	
    	Property connectProperty = config.get(config.CATEGORY_GENERAL, "connectOnStart", false);
    	connectProperty.comment = "Set this to true if you want to connect back to the server after cohering is done and Minecraft loads again."
    			+ "\nThis is not quite ready yet, so enable this at your own risk.";
    	connectOnStart = connectProperty.getBoolean();
    	
    	Property addressProperty = config.get(config.CATEGORY_GENERAL, "connectToServer", "null");
    	addressProperty.comment = "This tells coherence what server to connect to on start if connectOnStart is true. "
    							+ "DON'T EDIT THIS VARIABLE UNLESS YOU KNOW WHAT YOU ARE DOING.";
    	if (!addressProperty.getString().equals("null")) {
    		address = addressProperty.getString();
    		new PostCohere(address);
    		addressProperty.set("null");
    		postCohered = true;
    	}
    	
    	config.save();
    }
    
    @SideOnly(Side.CLIENT)
    @EventHandler
	public void init(FMLInitializationEvent event) {
		FMLCommonHandler.instance().bus().register(this);
	}
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
	public void clientTick(ClientTickEvent event) {
    	ticks++;
    	if (ticks == activationTicks && postCohered && connectOnStart) {
    		connected = true;
    		Minecraft mc = Minecraft.getMinecraft();
    		mc.displayGuiScreen(new GuiConnecting(new GuiScreen(), mc, new ServerData("Server", address + ":25565")));
    		return;
    	}
	}
    
    
    //=========================================END CLIENT SIDE CODE=================================================================
    
    //=========================================SERVER SIDE CODE=====================================================================
    @EventHandler
    @SideOnly(Side.SERVER)
    public void PostInit(FMLPostInitializationEvent event) throws IOException
    {
    	Server server = new Server();
    }
    //=========================================END SERVER SIDE CODE=================================================================
}
