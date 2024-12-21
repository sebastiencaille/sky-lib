package ch.scaille.tcwriter.gui.frame;

import java.awt.Component;
import java.util.logging.Level;

import javax.swing.JOptionPane;

import ch.scaille.util.helpers.Logs;

public class ExceptionHelper {
	
	public static void handleException(Component parent, final Exception ex) {
		Logs.of(ExceptionHelper.class).log(Level.WARNING, "Unable to execute action", ex);
		JOptionPane.showMessageDialog(parent, "Unable to execution action: " + ex.getMessage());
	}

}
