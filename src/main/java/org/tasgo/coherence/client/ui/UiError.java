package org.tasgo.coherence.client.ui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;

/**
 * Error screen for Coherence.
 */
@SideOnly(Side.CLIENT)
public class UiError extends GuiScreen
{
    private UiBasicCallback callback;
    private String error;

    public UiError(UiBasicCallback callback, String err)
    {
        this.callback = callback;
        this.error = err;
    }

    public static void crash(final GuiScreen parent, Exception e) {
        FMLClientHandler.instance().showGuiScreen(new UiError(new UiBasicCallback() {
            @Override
            public void onClick() {
                FMLClientHandler.instance().showGuiScreen(parent);
            }
        }, e.getMessage()));
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        super.initGui();
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, 140, I18n.format("gui.cancel", new Object[0])));
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        //this.drawGradientRect(0, 0, this.width, this.height, -12574688, -11530224);
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, "Error occured while trying to synchronize:", this.width / 2, 90, 16777215);
        this.drawCenteredString(this.fontRendererObj, this.error, this.width / 2, 100, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    /**
     * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(GuiButton button) throws IOException
    {
        callback.onClick();
    }
}
