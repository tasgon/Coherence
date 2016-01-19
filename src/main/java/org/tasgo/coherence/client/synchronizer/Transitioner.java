package org.tasgo.coherence.client.synchronizer;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.FMLClientHandler;
import org.tasgo.coherence.client.ui.UiYesNoCallback;

/**
 * Created by Tasgo on 1/18/16.
 */
public class Transitioner implements UiYesNoCallback {
    private Initiator initiator;

    public Transitioner(Initiator initiator) {
        this.initiator = initiator;
    }

    @Override
    public void onClick(boolean result) {
        if (result)
            new Synchronizer(initiator).start();
        else
            FMLClientHandler.instance().showGuiScreen(initiator.parent);
    }
}
