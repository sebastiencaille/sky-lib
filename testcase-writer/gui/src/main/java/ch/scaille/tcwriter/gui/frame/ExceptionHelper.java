package ch.scaille.tcwriter.gui.frame;

import lombok.extern.java.Log;

import java.awt.Component;
import java.util.logging.Level;

import javax.swing.JOptionPane;

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
