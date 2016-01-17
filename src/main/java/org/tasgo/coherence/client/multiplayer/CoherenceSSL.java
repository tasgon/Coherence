package org.tasgo.coherence.client.multiplayer;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.ServerListEntryLanScan;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.network.LanServerDetector;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.tasgo.coherence.client.ui.UiMultiplayer;

import java.util.List;

@SideOnly(Side.CLIENT)
public class CoherenceSSL extends GuiListExtended
{
    private final UiMultiplayer owner;
    private final List<CoherenceSLEN> field_148198_l = Lists.<CoherenceSLEN>newArrayList();
    private final List<CoherenceSLELD> field_148199_m = Lists.<CoherenceSLELD>newArrayList();
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
        if (index < this.field_148198_l.size())
        {
            return (GuiListExtended.IGuiListEntry)this.field_148198_l.get(index);
        }
        else
        {
            index = index - this.field_148198_l.size();

            if (index == 0)
            {
                return this.lanScanEntry;
            }
            else
            {
                --index;
                return (GuiListExtended.IGuiListEntry)this.field_148199_m.get(index);
            }
        }
    }

    public int getSize()
    {
        return this.field_148198_l.size() + 1 + this.field_148199_m.size();
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

    public int func_148193_k()
    {
        return this.selectedSlotIndex;
    }

    public void func_148195_a(ServerList p_148195_1_)
    {
        this.field_148198_l.clear();

        for (int i = 0; i < p_148195_1_.countServers(); ++i)
        {
            this.field_148198_l.add(new CoherenceSLEN(this.owner, p_148195_1_.getServerData(i)));
        }
    }

    public void func_148194_a(List<LanServerDetector.LanServer> p_148194_1_)
    {
        this.field_148199_m.clear();

        for (LanServerDetector.LanServer lanserverdetector$lanserver : p_148194_1_)
        {
            this.field_148199_m.add(new CoherenceSLELD(this.owner, lanserverdetector$lanserver));
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