package org.tasgo.coherence.client.ui;

import javax.swing.JOptionPane;

public class Request {

	public static boolean getYesNo(String query, String title) {
		int dialogButton = JOptionPane.YES_NO_OPTION;
	    JOptionPane.showConfirmDialog (null, query, title, dialogButton);
	    
	    if (dialogButton == JOptionPane.YES_OPTION)
	    	return true;
	    else
	    	return false;
	}

	public static boolean getYesNo(String query) {
		return getYesNo(query, "Warning");
	}

}
