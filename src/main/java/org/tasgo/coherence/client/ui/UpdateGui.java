package org.tasgo.coherence.client.ui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.net.URI;

public class UpdateGui extends GuiScreen {
	
	private GuiScreen parentGuiScreen;
	private String version;
    
    public UpdateGui(GuiScreen parGuiScreen, String ver)
    {
        parentGuiScreen = parGuiScreen;
        version = ver;
    }
    
    @SuppressWarnings("unchecked")
	@Override
    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);
        buttonList.clear();
        this.buttonList.add(new GuiOptionButton(0, this.width / 2 - 155, this.height / 6 + 96, "Take me there!")); //TODO: Add multilingual support
        this.buttonList.add(new GuiOptionButton(1, this.width / 2 - 155 + 160, this.height / 6 + 96, "I'll pass."));
    }

    @Override
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        if (button.enabled)
        {
        	if (button.id == 0) {
				try {
					Desktop.getDesktop().browse(new URI("https://github.com/tasgoon/Coherence/releases/" + version));
					//FMLCommonHandler.instance().exitJava(0, false);
				} catch (Exception e) {
					e.printStackTrace();
				}
        	}
            this.mc.displayGuiScreen(this.parentGuiScreen);
        }
    }
    
    @Override
    public void drawScreen(int x, int y, float renderPartialTicks)
    {
        this.drawDefaultBackground();
        
        this.drawCenteredString(this.fontRendererObj, "There is a new update to Coherence: " + version, this.width / 2, 82, 0xFFFFFF);
        this.drawCenteredString(this.fontRendererObj, "Get it at https://www.github.com/tasgoon/Coherence/releases/" + version, this.width / 2, 94, 0xFFFFFF);
        
        /*GL11.glEnable(GL11.GL_BLEND);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        
        this.drawTexturedModalRect(this.width / 2 - 168 / 2, 0, 0, 0, 168, 80);
        
        GL11.glDisable(GL11.GL_BLEND);*/
       
        super.drawScreen(x, y, renderPartialTicks);
    }
}
