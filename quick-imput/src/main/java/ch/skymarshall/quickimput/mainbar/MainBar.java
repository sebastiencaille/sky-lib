package ch.skymarshall.quickimput.mainbar;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import ch.skymarshall.quickimput.ImputController;
import ch.skymarshall.quickimput.ImputEntryAction;
import ch.skymarshall.quickimput.tables.ImputTables;

public class MainBar extends JFrame {

    private final MainBarModel      model;
    private final MainBarController controller;

    public MainBar(final ImputController imputController) {
        model = new MainBarModel(imputController.getModel());
        controller = new MainBarController(imputController);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setUndecorated(true);
        setAlwaysOnTop(true);
        setLocation(500, 500);

        final JLabel startImput = new JLabel("B");
        final JLabel stopImput = new JLabel("E");
        final JLabel today = new JLabel("T");

        final JComponent[] all = new JComponent[] { startImput, stopImput, today };

        startImput.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(final MouseEvent e) {
                final PopupController closer = new PopupController(ImputTables.textOnlyTable(model.startList),
                        new ImputEntryAction(model.startList, controller.startImputAction()), null)//
                        .exclude(allBut(all, startImput)).include(getContentPane(), startImput);
                controller.openPopup(MainBar.this, closer);
            }

        });

        stopImput.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(final MouseEvent e) {
                final PopupController popupController = new PopupController(ImputTables
                        .inProgressTable(model.todayInProgress), new ImputEntryAction(model.todayInProgress, controller
                        .stopImputAction()), null)//
                .exclude(allBut(all, stopImput)).include(getContentPane(), stopImput);
                controller.openPopup(MainBar.this, popupController);
            }

        });

        today.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(final MouseEvent e) {
                final PopupController popupController = new PopupController(ImputTables.completeTable(model.today),
                        null, controller.openSummaryView())//
                        .exclude(allBut(all, today)).include(getContentPane(), today);
                controller.openPopup(MainBar.this, popupController);
            }

        });

        final MouseAdapter moverListener = new MouseAdapter() {

            private Point startDragInFrame;

            @Override
            public void mousePressed(final MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e) && e.getClickCount() == 2) {
                    System.exit(0);
                }
                controller.closePopup();
                startDragInFrame = SwingUtilities.convertPoint(e.getComponent(), e.getPoint().x, e.getPoint().y,
                        MainBar.this);
            }

            @Override
            public void mouseDragged(final MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    final Point locationOnScreen = e.getLocationOnScreen();
                    locationOnScreen.translate(-startDragInFrame.x, -startDragInFrame.y);
                    setLocation(locationOnScreen);
                }
            }
        };

        final JPanel mainPanel = new JPanel(new FlowLayout());
        mainPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        for (final JComponent comp : all) {
            mainPanel.add(comp);
            comp.addMouseListener(moverListener);
            comp.addMouseMotionListener(moverListener);
        }
        getContentPane().add(mainPanel);
    }

    protected static Component[] allBut(final JComponent[] all, final Component component) {
        final Component[] res = new Component[all.length - 1];
        for (int i = 0; i < all.length; i++) {
            if (all[i] != component) {
                res[i] = all[i];
            } else {
                System.arraycopy(all, i + 1, res, i, all.length - i - 1);
                break;
            }
        }
        return res;
    }

    public void start() {
        validate();
        pack();
        setVisible(true);
    }

}
