package ch.skymarshall.quickimput.mainbar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import ch.skymarshall.quickimput.ImputController;
import ch.skymarshall.quickimput.ImputEntryApplier;
import ch.skymarshall.quickimput.ImputModel;
import ch.skymarshall.quickimput.model.EntryType;
import ch.skymarshall.quickimput.model.ImputEntry;
import ch.skymarshall.quickimput.monthreport.MonthReportView;

public class MainBarController {

    private final ImputModel      model;
    private PopupController       popup = null;
    private final ImputController controller;

    public MainBarController(final ImputController controller) {
        this.controller = controller;
        this.model = controller.getModel();
    }

    public void openPopup(final JFrame frameAdapter, final PopupController popupController) {
        if (popup != null) {
            popup.closePopup();
        }
        popup = popupController;
        popup.openPopup(this, frameAdapter);
    }

    public ImputEntryApplier startImputAction() {
        return new ImputEntryApplier() {
            @Override
            public void apply(final ImputEntry imput) {
                final ImputEntry start = imput.start();
                if (imput.getEntryType() == EntryType.TEMPLATE_TEXT_TO_COMPLETE) {
                    final String newText = JOptionPane.showInputDialog("Complete text", start.getText());
                    start.setText(newText);
                }
                model.imputations.insert(start);
                controller.save();
            }
        };
    }

    public ImputEntryApplier stopImputAction() {
        return new ImputEntryApplier() {
            @Override
            public void apply(final ImputEntry imput) {
                final ImputEntry toStop = model.imputations.findForEdition(imput);
                toStop.stop();
                model.imputations.stopEditingValue();
                controller.save();
            }
        };
    }

    public void closePopup() {
        if (popup != null) {
            popup.closePopup();
        }
    }

    public void popupClosed() {
        popup = null;
    }

    public ActionListener openSummaryView() {
        return new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                new MonthReportView(controller).start();
            }
        };
    }
}
