package org.tasgo.coherence.client.ui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Credit Rockdude101
 */
@SideOnly(Side.CLIENT)
public class UiMainMenuRedirect extends GuiMainMenu
{
    public GuiScreen guiToShow;

    /**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    protected void keyTyped(char par1, int par2) {}
    public UiMainMenuRedirect(GuiScreen g){
        guiToShow=g;
    }
    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        super.initGui();
    }

    protected void actionPerformed(GuiButton butt)
    {
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3)
    {
        super.drawScreen(par1, par2, par3);

        mc.displayGuiScreen(guiToShow);
    }
}
