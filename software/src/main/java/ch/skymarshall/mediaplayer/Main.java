package ch.skymarshall.mediaplayer;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import ch.skymarshall.mediaplayer.hmi.MediaFileController;
import ch.skymarshall.mediaplayer.hmi.MediaFileView;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

public class Main {

    public static void main(final String[] args) throws InvocationTargetException, InterruptedException {
        new Main().start();

    }

    public Main() {
    }

    private void start() throws InvocationTargetException, InterruptedException {
        SwingUtilities.invokeAndWait(new Runnable() {

            @Override
            public void run() {
                final JFrame frame = new JFrame();
                final MediaFileController mediaFileController = new MediaFileController();

                try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get("/", "home", "scaille", "Music"))) {
                    mediaFileController.getModel().getFileList()
                            .addValues(FluentIterable.from(stream).transform(new Function<Path, MediaFile>() {
                                @Override
                                public MediaFile apply(final Path path) {
                                    try {
                                        return new MediaFile(path);
                                    } catch (final IOException e) {
                                        throw new IllegalArgumentException("Invalid path: " + path, e);
                                    }
                                }
                            }).toList());
                } catch (final IOException e1) {
                    throw new IllegalStateException("Unable to scan folder: " + e1);
                }

                frame.getContentPane().add(new MediaFileView(mediaFileController));
                mediaFileController.start();
                frame.validate();
                frame.pack();
                frame.setVisible(true);
            }
        });

    }
}
