package ch.scaille.testing.testpilot.swing;

import static ch.scaille.testing.testpilot.factories.Reporting.checkingValue;
import static ch.scaille.testing.testpilot.factories.Reporting.settingValue;
import static ch.scaille.testing.testpilot.factories.Reporting.text;

import javax.swing.JTable;

import ch.scaille.testing.testpilot.builder.AssertionResult;

import org.junit.jupiter.api.Assertions;

public class JTableAssertionBuilder extends SwingAssertionBuilder<JTable, JTableAssertionBuilder, JTableAssertionBuilder.SwingAssertion> {

    public static class SwingAssertion extends SwingAssertionBuilder.SwingAssertion<JTable> {

        protected SwingAssertion(JTableAssertionBuilder builder) {
            super(builder);
        }

        public AssertionResult<JTable> selectRow(final int index) {
            return configure(polling -> polling.reporting(text("select row " + index)))
                    .applied(t -> t.setRowSelectionInterval(index, index));
        }

        public AssertionResult<JTable> editValue(final int row, final int column, final String value) {
            return configure(polling -> polling.reporting(settingValue("at row/column " + row + '/' + column, value)))
                    .applied(t -> t.setValueAt(value, row, column));
        }

        public AssertionResult<JTable> editValueOnSelectedRow(final int column, final String value) {
            return configure(polling -> polling.reporting(settingValue("at selected row, column " + column, value)))
                    .applied(t ->
                        t.setValueAt(value, t.getSelectedRow(), column));
        }

        public AssertionResult<JTable> assertValue(final int row, final int column, final String expected) {
            return configure(polling -> polling.reporting(checkingValue("at row/column " + row + '/' + column, expected)))
                    .assertedCtxt(pc -> Assertions.assertEquals(expected, pc.component().getValueAt(row, column),
                            pc.description()));
        }

        public AssertionResult<JTable> assertValueOnSelectedRow(final int column, final String expected) {
            return configure(polling -> polling.reporting(checkingValue("at selected row, column " + column, expected)))
                    .assertedCtxt(pc -> Assertions.assertEquals(expected,
                            pc.component().getValueAt(pc.component().getSelectedRow(), column),
                            pc.description()));
        }
    }

    public JTableAssertionBuilder(final SwingPilot pilot, final String name) {
        super(new SwingComponentPilot<>(pilot, JTable.class, name));
    }

    @Override
    protected SwingAssertion createPoller() {
        return new SwingAssertion(this);
    }
}
