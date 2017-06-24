/*******************************************************************************
 * Copyright (c) 2017 Sebastien Caille.
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms are permitted
 *  provided that the above Copyrightnotice and this paragraph are
 *  duplicated in all such forms and that any documentation,
 *  advertising materials, and other materials related to such
 *  distribution and use acknowledge that the software was developed
 *  by Sebastien Caille.  The name of Sebastien Caille may not be used to endorse or promote products derived
 *  from this software without specific prior written permission.
 *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ******************************************************************************/
package org.skymarshall.hmi.swing;

import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.skymarshall.hmi.swing.model.ListModelTableModel;

/**
 * Table column model based on per-column contribution.
 * <p>
 *
 * @author Sebastien Caille
 *
 * @param <C>
 *            enum that defines the columns (see {@link ListModelTableModel}
 */
public abstract class ContributionTableColumn<C extends Enum<C>> extends TableColumn {

	private ContributionTableColumnModel<C> model;

	private final C column;

	public abstract void contribute(ColumnContribution contrib);

	public abstract void computeWidth(ColumnContribution contrib);

	public ContributionTableColumn(final C column) {
		this.column = column;
	}

	public C getColumn() {
		return column;
	}

	@Override
	public void setWidth(final int width) {
		model.update();
	}

	public void setModel(final ContributionTableColumnModel<C> model) {
		this.model = model;
	}

	public void setComputedWidth(final int width) {
		super.setWidth(width);
	}

	/**
	 * Column that occupies the gap left open by the other columns
	 *
	 * @param columnIdentifier
	 *            identifier of the column
	 * @param percent
	 *            percent of the gap used by this column
	 * @param renderer
	 *            column renderer
	 * @return a table column
	 */
	public static <C extends Enum<C>> ContributionTableColumn<C> gapColumn(final C columnIdentifier, final int percent,
			final TableCellRenderer renderer) {
		final ContributionTableColumn<C> column = new ContributionTableColumn<C>(columnIdentifier) {
			@Override
			public void contribute(final ColumnContribution contribution) {
				contribution.gapColumnCount++;
				contribution.gapTotalFraction += percent;
			}

			@Override
			public void computeWidth(final ColumnContribution contribution) {
				final int gap = contribution.unallocateWidth;
				super.setComputedWidth((int) (gap * (((float) percent) / (contribution.gapTotalFraction))));
			}
		};
		column.setCellRenderer(renderer);
		return column;
	}

	/**
	 * Column with fixed length
	 *
	 * @param columnIdentifier
	 *            identifier of the column
	 * @param fixedWidth
	 *            with of the column
	 * @param renderer
	 *            column renderer
	 * @return a table column
	 */
	public static <C extends Enum<C>> ContributionTableColumn<C> fixedColumn(final C columnIdentifier,
			final int fixedWidth, final TableCellRenderer renderer) {
		final ContributionTableColumn<C> column = new ContributionTableColumn<C>(columnIdentifier) {
			@Override
			public void contribute(final ColumnContribution contribution) {
				contribution.allocatedWidth += fixedWidth;
			}

			@Override
			public void computeWidth(final ColumnContribution contrib) {
				super.setComputedWidth(fixedWidth);

			}
		};
		column.setCellRenderer(renderer);
		return column;
	}

}
