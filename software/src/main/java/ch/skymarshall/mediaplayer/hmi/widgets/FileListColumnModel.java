package ch.skymarshall.mediaplayer.hmi.widgets;

import javax.swing.JTable;

import org.skymarshall.hmi.swing17.ContributionTableColumnModel;

public class FileListColumnModel extends ContributionTableColumnModel<FileListTableModel.Columns> {

    public FileListColumnModel(final JTable table) {
        super(table);
    }

}
