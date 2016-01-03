package org.tasgo.coherence.client.ui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import net.minecraft.client.Minecraft;

public class Request {

	public static boolean getYesNo(String query, String title) {
		if (Minecraft.getMinecraft().isFullScreen())
			Minecraft.getMinecraft().toggleFullscreen();
		
		final JOptionPane optionPane = new JOptionPane(query, JOptionPane.QUESTION_MESSAGE,
				JOptionPane.YES_NO_OPTION);

		final JDialog dialog = optionPane.createDialog(title);
		optionPane.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				String prop = e.getPropertyName();
				if (dialog.isVisible() && (e.getSource() == optionPane) && (prop.equals(JOptionPane.VALUE_PROPERTY))) {
					dialog.setVisible(false);
				}
			}
		});
		dialog.pack();
		dialog.setVisible(true);
		
		try {
			int value = ((Integer) optionPane.getValue()).intValue();
			if (value == JOptionPane.YES_OPTION) {
				return true;
			} else {
				return false;
			}
		}
		catch (Exception e) { //To handle if the user closes the dialog
			return false;
		}
	}

	public static boolean getYesNo(String query) {
		return getYesNo(query, "Warning");
	}

}
