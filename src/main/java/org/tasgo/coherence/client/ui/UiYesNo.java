package org.tasgo.coherence.client.ui;

import com.google.common.collect.Lists;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;
import java.util.List;

@SideOnly(Side.CLIENT)
public class UiYesNo extends GuiScreen
{
    /** A reference to the screen object that created  Used for navigating between screens. */
    protected UiYesNoCallback callback;
    protected String message;
    private final List<String> lines = Lists.<String>newArrayList();
    /** The text shown for the first button in GuiYesNo */
    protected String confirmButtonText;
    /** The text shown for the second button in GuiYesNo */
    protected String cancelButtonText;
    private int ticksUntilEnable;

    public UiYesNo( UiYesNoCallback yesNoCallback, String msg)
    {
        callback = yesNoCallback;
        message = msg;
        confirmButtonText = I18n.format("gui.yes", new Object[0]);
        cancelButtonText = I18n.format("gui.no", new Object[0]);
    }

    public UiYesNo(UiYesNoCallback yesNoCallback, String msg, String confirmText, String cancelText)
    {
        callback = yesNoCallback;
        message = msg;
        confirmButtonText = confirmText;
        cancelButtonText = cancelText;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        buttonList.add(new GuiOptionButton(0, width / 2 - 155, height / 6 + 96, confirmButtonText));
        buttonList.add(new GuiOptionButton(1, width / 2 - 155 + 160, height / 6 + 96, cancelButtonText));
        lines.clear();
        for (String line : message.split("\n"))
            lines.addAll(fontRendererObj.listFormattedStringToWidth(line, width - 50));
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(GuiButton button) throws IOException
    {
        callback.onClick(button.id == 0);
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        drawDefaultBackground();
        int yPos = 70;

        for (String s : lines)
        {
            drawCenteredString(fontRendererObj, s, width / 2, yPos, 16777215);
            yPos += fontRendererObj.FONT_HEIGHT;
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    /**
     * Sets the number of ticks to wait before enabling the buttons.
     */
    public void setButtonDelay(int delay)
    {
        ticksUntilEnable = delay;

        for (GuiButton guibutton : buttonList)
        {
            guibutton.enabled = false;
        }
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        super.updateScreen();

        if (--ticksUntilEnable == 0)
        {
            for (GuiButton guibutton : buttonList)
            {
                guibutton.enabled = true;
            }
        }
    }
}
