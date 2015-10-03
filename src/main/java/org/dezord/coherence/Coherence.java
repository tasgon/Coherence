package org.dezord.coherence;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dezord.coherence.client.Cohere;
import org.dezord.coherence.client.JavaCommandBuilder;
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
    public static final String VERSION = "1.7a06";
    public static final int activationTicks = 60;
    public static boolean connectOnStart;
    
    public static boolean postCohered = false;
    public static File configName;
    public static String modsToKeep;
    
    private static final Logger logger = LogManager.getLogger("Coherence");
    private int ticks = 0;
    private boolean connected = true;
    
    public static String address;
    public static int port;
    
    //=========================================CLIENT SIDE CODE=================================================================
    @EventHandler
    @SideOnly(Side.CLIENT)
    public void PreInit(FMLPreInitializationEvent event) throws IOException {
    	PostCohere.startCohereUndoer();
    	try {
			Cohere.detectCrash();
		} catch (Exception e) {}
    }
    
    @SideOnly(Side.CLIENT)
    @EventHandler
	public void init(FMLInitializationEvent event) {
		FMLCommonHandler.instance().bus().register(this);
		logger.info(Coherence.class.getProtectionDomain().getCodeSource().getLocation().getPath());
	}
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
	public void clientTick(ClientTickEvent event) {
    	ticks++;
    	if (ticks == activationTicks && postCohered && connectOnStart) {
    		connected = true;
    		Minecraft mc = Minecraft.getMinecraft();
    		mc.displayGuiScreen(new GuiConnecting(mc.currentScreen, mc, address, 25565));
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
