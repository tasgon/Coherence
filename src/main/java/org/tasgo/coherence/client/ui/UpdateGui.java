package org.tasgo.coherence.client.ui;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

public class UpdateGui extends GuiScreen {
	public class Releases {
		String tag_name, body;
	}
	
	private GuiScreen parentGuiScreen;
	private String version;
    
    public UpdateGui(GuiScreen parGuiScreen, String ver)
    {
        parentGuiScreen = parGuiScreen;
        version = ver;
    }
    
    @Override
    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);
        buttonList.clear();
        buttonList.add(new GuiButton(0, this.width / 2 - 175, this.height - 24, 350, 20, I18n.format("OK")));
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
            if (button.id == 0)
            {
                this.mc.displayGuiScreen(this.parentGuiScreen);
            }
        }
    }
    
    @Override
    public void drawScreen(int x, int y, float renderPartialTicks)
    {
        this.drawDefaultBackground();
        
        this.drawCenteredString(this.fontRendererObj, "There is a new update to Coherence: " + version, this.width / 2, 82, 0xFFFFFF);
        this.drawCenteredString(this.fontRendererObj, "Get it at https://github.com/tasgoon/Coherence/releases/" + version, this.width / 2, 94, 0xFFFFFF);
        
        /*GL11.glEnable(GL11.GL_BLEND);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        
        this.drawTexturedModalRect(this.width / 2 - 168 / 2, 0, 0, 0, 168, 80);
        
        GL11.glDisable(GL11.GL_BLEND);*/
       
        super.drawScreen(x, y, renderPartialTicks);
    }
}
