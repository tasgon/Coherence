package org.tasgo.coherence.client.handlers;

import com.google.common.reflect.TypeToken;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.tasgo.coherence.Coherence;
import org.tasgo.coherence.client.ui.UiUpdate;
import org.tasgo.coherence.common.Library;
import org.tasgo.coherence.common.github.GithubRelease;

import java.lang.reflect.Type;
import java.net.URL;
import java.util.List;

public class UpdateHandler {
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void checkMainMenu(GuiOpenEvent event) {
		String update = getUpdate();
		if (event.gui instanceof GuiMainMenu && update != null) {
			event.gui = new UiUpdate(event.gui, update);
			MinecraftForge.EVENT_BUS.unregister(this);
		}
	}
	
	private String getUpdate() {
		Type type = new TypeToken<List<GithubRelease>>(){}.getType();
		List<GithubRelease> releases;
		
		try {
			releases = Library.<List<GithubRelease>>urlToJson(new URL("https://api.github.com/repos/tasgoon/Coherence/releases"), type);
			for (GithubRelease release : releases) {
				String ver = release.body.substring(0, release.body.indexOf("\r")).replaceAll("Compatible with: ", "");
				//System.out.println("Version: " + release.tag_name + " for " + ver);
				if (ver.equals(MinecraftForge.MC_VERSION)) {
					//System.out.println("Version " + release.tag_name + " is compatible with this version of Minecraft.");
					if (release.tag_name != Coherence.VERSION_STRING) {
						System.out.println("A newer version of Coherence has been detected: " + release.tag_name);
						return release.tag_name;
					}
					else
						return null;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
