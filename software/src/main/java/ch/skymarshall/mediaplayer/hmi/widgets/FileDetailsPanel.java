package ch.skymarshall.mediaplayer.hmi.widgets;

import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.skymarshall.hmi.mvc.converters.Converters;
import org.skymarshall.hmi.mvc.properties.ObjectProperty;
import org.skymarshall.hmi.swing17.bindings.SwingBindings;

import ch.skymarshall.mediaplayer.MediaFile;
import ch.skymarshall.mediaplayer.MediaFileHmiModel;

public class FileDetailsPanel extends JPanel {

	public FileDetailsPanel(final ObjectProperty<MediaFile> property,
			final MediaFileHmiModel model) {

		setLayout(new GridLayout(3, 1));

		// add(bold(new JLabel("Filename")));
		final JLabel filename = new JLabel();
		model.getFileNameProperty().bind(SwingBindings.value(filename));
		enabler(property, filename);
		add(filename);

		// add(bold(new JLabel("Type")));
		final JLabel filetype = new JLabel();
		model.getTypeProperty().bind(SwingBindings.value(filetype));
		enabler(property, filetype);
		add(filetype);

		// add(bold(new JLabel("Size")));
		final JLabel fileSize = new JLabel();
		model.getSizeProperty().bind(Converters.<Long> numberToSize())
				.bind(SwingBindings.value(fileSize));
		enabler(property, fileSize);
		add(fileSize);
	}

	private void enabler(final ObjectProperty<MediaFile> property,
			final JLabel filename) {
		property.bind(Converters.<MediaFile> isNotNull()).bind(
				SwingBindings.enabled(filename));
	}

	private static <T extends Component> T bold(final T component) {
		component.setFont(component.getFont().deriveFont(Font.BOLD));
		return component;
	}

}
