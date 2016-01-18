/*
 * The FML Forge Mod Loader suite. Copyright (C) 2012 cpw
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */
package org.tasgo.coherence.client.multiplayer;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.ExtendedServerListData;
import net.minecraftforge.fml.client.FMLClientHandler;
import org.tasgo.coherence.client.Client;

import java.lang.reflect.Field;
import java.util.Map;


/**
 * FML, modified for Coherence.
 */
public class CoherenceEnhancer
{

    public static String enhanceServerListEntry(CoherenceSLEN serverListEntry, ServerData serverEntry, int x, int width, int y, int relativeMouseX, int relativeMouseY)
    {
        String tooltip;
        int textureIndex;
        boolean blocked = false;
        Map<ServerData, ExtendedServerListData> sDT = (Map<ServerData, ExtendedServerListData>) getPrivateField("serverDataTag");
        if (sDT.containsKey(serverEntry))
        {
            ExtendedServerListData extendedData = sDT.get(serverEntry);
            if ("FML".equals(extendedData.type) && extendedData.isCompatible)
            {
                textureIndex = 0;
                tooltip = String.format("Compatible FML modded server\n%d mods present", extendedData.modData.size());
            }
            else if ("FML".equals(extendedData.type) && Client.guaranteedCompatible(serverEntry.serverIP)) { //Added for Coherence
                textureIndex = 0;
                tooltip = String.format("Coherence-enabled FML modded server\n%d mods present", extendedData.modData.size());
            }
            else if("FML".equals(extendedData.type) && Client.maybeCompatible(serverEntry.serverIP)) { //Also added for Coherence
                textureIndex = 16;
                tooltip = String.format("Coherence-enabled FML modded server\nMay not be compatible\n%d mods present", extendedData.modData.size());
            }
            else if ("FML".equals(extendedData.type) && !extendedData.isCompatible)
            {
                textureIndex = 16;
                tooltip = String.format("Incompatible FML modded server\n%d mods present", extendedData.modData.size());
            }
            else if ("BUKKIT".equals(extendedData.type))
            {
                textureIndex = 32;
                tooltip = String.format("Bukkit modded server");
            }
            else if ("VANILLA".equals(extendedData.type))
            {
                textureIndex = 48;
                tooltip = String.format("Vanilla server");
            }
            else
            {
                textureIndex = 64;
                tooltip = String.format("Unknown server data");
            }
            blocked = extendedData.isBlocked;
        }
        else
        {
            return null;
        }
        FMLClientHandler.instance().getClient().getTextureManager().bindTexture((ResourceLocation) getPrivateField("iconSheet"));
        Gui.drawModalRectWithCustomSizedTexture(x + width - 18, y + 10, 0, (float)textureIndex, 16, 16, 256.0f, 256.0f);
        if (blocked)
        {
            Gui.drawModalRectWithCustomSizedTexture(x + width - 18, y + 10, 0, 80, 16, 16, 256.0f, 256.0f);
        }

        return relativeMouseX > width - 15 && relativeMouseX < width && relativeMouseY > 10 && relativeMouseY < 26 ? tooltip : null;
    }

    public static Object getPrivateField(String name) {
        try {
            Field f = FMLClientHandler.instance().getClass().getDeclaredField(name);
            f.setAccessible(true);
            return f.get(FMLClientHandler.instance());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}