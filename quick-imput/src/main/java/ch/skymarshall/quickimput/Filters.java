package ch.skymarshall.quickimput;

import java.time.LocalDate;

import org.skymarshall.hmi.model.IFilter;

import ch.skymarshall.quickimput.model.ImputEntry;

public class Filters {
    public static final IFilter<ImputEntry> todayCompleteFilter   = new IFilter<ImputEntry>() {
        @Override
        public boolean accept(final ImputEntry value) {
            if (value.getDuration() == null) {
                return false;
            }
            return LocalDate.now().atStartOfDay()
                    .isBefore(value.getStart());
        }
    };
    public static final IFilter<ImputEntry> todayInProgressFilter = new IFilter<ImputEntry>() {
        @Override
        public boolean accept(final ImputEntry value) {
            if (value.getDuration() != null) {
                return false;
            }
            return LocalDate.now().atStartOfDay()
                    .isBefore(value.getStart());
        }
    };
    public static IFilter<ImputEntry>       monthCompleteFilter   = new IFilter<ImputEntry>() {
                                                                      @Override
                                                                      public boolean accept(final ImputEntry value) {
                                                                          if (value.getDuration() == null) {
                                                                              return false;
                                                                          }
                                                                          return LocalDate.now().getMonth()
                                                                                  .equals(value.getStart().getMonth());
                                                                      }
                                                                  };

}
