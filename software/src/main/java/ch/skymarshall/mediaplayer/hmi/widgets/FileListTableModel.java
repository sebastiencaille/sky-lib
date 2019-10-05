package ch.skymarshall.mediaplayer.hmi.widgets;

import org.skymarshall.hmi.model.ListModel;
import org.skymarshall.hmi.swing17.model.ListModelTableModel;

import ch.skymarshall.mediaplayer.MediaFile;
import ch.skymarshall.mediaplayer.hmi.widgets.FileListTableModel.Columns;

public class FileListTableModel extends ListModelTableModel<MediaFile, Columns> {
    public enum Columns {
        FILENAME, SIZE
    }

    public FileListTableModel(final ListModel<MediaFile> model) {
        super(model, Columns.class);
    }

    @Override
    protected Object getValueAtColumn(final MediaFile object, final Columns column) {
        switch (column) {
        case FILENAME:
            return object.getFileName();
        case SIZE:
            return Long.valueOf(object.getSize());
        }
        return object;
    }

    @Override
    protected void setValueAtColumn(final MediaFile object, final Columns column, final Object value) {
        // Read-only
    }
}
