package ch.scaille.tcwriter.gui.frame;

import java.awt.Component;
import java.util.logging.Level;

import javax.swing.JOptionPane;

import lombok.extern.java.Log;

@Log
public class ExceptionHelper {
	
	private ExceptionHelper() {
		// noop
	}
	
	public static void handleException(Component parent, final Exception ex) {
		log.log(Level.WARNING, "Unable to execute action", ex);
		JOptionPane.showMessageDialog(parent, "Unable to execution action: " + ex.getMessage());
	}

}
