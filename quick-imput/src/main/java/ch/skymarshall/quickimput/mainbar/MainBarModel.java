package ch.skymarshall.quickimput.mainbar;

import java.util.Comparator;

import org.skymarshall.hmi.model.ListModel;
import org.skymarshall.hmi.model.views.ListViews;

import ch.skymarshall.quickimput.Filters;
import ch.skymarshall.quickimput.ImputModel;
import ch.skymarshall.quickimput.model.ImputEntry;

public class MainBarModel {
    public static final String                 MONTH_REPORT = "Month report";
    public static final Comparator<ImputEntry> TEXT_SORTING = new Comparator<ImputEntry>() {
        @Override
        public int compare(final ImputEntry o1,
                final ImputEntry o2) {
            return o1.getText().compareTo(o2.getText());
        }
    };
    final ListModel<ImputEntry>                startList;

    final ListModel<ImputEntry>                todayInProgress;

    final ListModel<ImputEntry>                today;

    public MainBarModel(final ImputModel model) {
        startList = new ListModel<>(model.staticImputations, ListViews.sorted(TEXT_SORTING));
        todayInProgress = new ListModel<>(model.imputations, ListViews.filtered(Filters.todayInProgressFilter));
        today = new ListModel<>(model.imputations, ListViews.filtered(Filters.todayCompleteFilter));
    }

}
