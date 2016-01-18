package org.tasgo.coherence.client.multiplayer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.network.LanServerDetector;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.tasgo.coherence.client.ui.UiMultiplayer;

@SideOnly(Side.CLIENT)
public class CoherenceSLELD implements GuiListExtended.IGuiListEntry
{
    private final UiMultiplayer uiMultiplayer;
    protected final Minecraft mc;
    protected final LanServerDetector.LanServer lanServer;
    private long curTime = 0L;

    protected CoherenceSLELD(UiMultiplayer multiplayer, LanServerDetector.LanServer server)
    {
        this.uiMultiplayer = multiplayer;
        this.lanServer = server;
        this.mc = Minecraft.getMinecraft();
    }

    public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected)
    {
        this.mc.fontRendererObj.drawString(I18n.format("lanServer.title", new Object[0]), x + 32 + 3, y + 1, 16777215);
        this.mc.fontRendererObj.drawString(this.lanServer.getServerMotd(), x + 32 + 3, y + 12, 8421504);

        if (this.mc.gameSettings.hideServerAddress)
        {
            this.mc.fontRendererObj.drawString(I18n.format("selectServer.hiddenAddress", new Object[0]), x + 32 + 3, y + 12 + 11, 3158064);
        }
        else
        {
            this.mc.fontRendererObj.drawString(this.lanServer.getServerIpPort(), x + 32 + 3, y + 12 + 11, 3158064);
        }
    }

    /**
     * Returns true if the mouse has been pressed on this control.
     */
    public boolean mousePressed(int slotIndex, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_, int p_148278_6_)
    {
        this.uiMultiplayer.selectServer(slotIndex);

        if (Minecraft.getSystemTime() - this.curTime < 250L)
        {
            this.uiMultiplayer.connectToSelected();
        }

        this.curTime = Minecraft.getSystemTime();
        return false;
    }

    public void setSelected(int p_178011_1_, int p_178011_2_, int p_178011_3_)
    {
    }

    /**
     * Fired when the mouse button is released. Arguments: index, x, y, mouseEvent, relativeX, relativeY
     */
    public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY)
    {
    }

    public LanServerDetector.LanServer getLanServer()
    {
        return this.lanServer;
    }
}