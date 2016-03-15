package org.tasgoon.coherence.client.multiplayer;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.ServerListEntryLanScan;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.network.LanServerDetector;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.tasgoon.coherence.client.ui.UiMultiplayer;

import java.util.List;

/**
 * ServerSelectionList, modified for Coherence.
 */
@SideOnly(Side.CLIENT)
public class CoherenceSSL extends GuiListExtended
{
    private final UiMultiplayer owner;
    private final List<CoherenceSLEN> slenList = Lists.<CoherenceSLEN>newArrayList();
    private final List<CoherenceSLELD> sleldList = Lists.<CoherenceSLELD>newArrayList();
    private final GuiListExtended.IGuiListEntry lanScanEntry = new ServerListEntryLanScan();
    private int selectedSlotIndex = -1;

    public CoherenceSSL(UiMultiplayer ownerIn, Minecraft mcIn, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn)
    {
        super(mcIn, widthIn, heightIn, topIn, bottomIn, slotHeightIn);
        this.owner = ownerIn;
    }

    /**
     * Gets the IGuiListEntry object for the given index
     */
    public GuiListExtended.IGuiListEntry getListEntry(int index)
    {
        if (index < this.slenList.size())
        {
            return (GuiListExtended.IGuiListEntry)this.slenList.get(index);
        }
        else
        {
            index = index - this.slenList.size();

            if (index == 0)
            {
                return this.lanScanEntry;
            }
            else
            {
                --index;
                return (GuiListExtended.IGuiListEntry)this.sleldList.get(index);
            }
        }
    }

    public int getSize()
    {
        return this.slenList.size() + 1 + this.sleldList.size();
    }

    public void setSelectedSlotIndex(int selectedSlotIndexIn)
    {
        this.selectedSlotIndex = selectedSlotIndexIn;
    }

    /**
     * Returns true if the element passed in is currently selected
     */
    protected boolean isSelected(int slotIndex)
    {
        return slotIndex == this.selectedSlotIndex;
    }

    public int getSelectedSlot()
    {
        return this.selectedSlotIndex;
    }

    public void updateServerList(ServerList serverList)
    {
        this.slenList.clear();

        for (int i = 0; i < serverList.countServers(); ++i)
        {
            this.slenList.add(new CoherenceSLEN(this.owner, serverList.getServerData(i)));
        }
    }

    public void updateServerList(List<LanServerDetector.LanServer> lanServerList)
    {
        this.sleldList.clear();

        for (LanServerDetector.LanServer lanServer : lanServerList)
        {
            this.sleldList.add(new CoherenceSLELD(this.owner, lanServer));
        }
    }

    protected int getScrollBarX()
    {
        return super.getScrollBarX() + 30;
    }

    /**
     * Gets the width of the list
     */
    public int getListWidth()
    {
        return super.getListWidth() + 85;
    }
}