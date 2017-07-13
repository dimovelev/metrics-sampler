package org.metricssampler.extensions.jmx;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularType;
import java.util.ArrayList;
import java.util.List;

import static org.metricssampler.util.Preconditions.checkArgumentNotNull;

public class RowPathSegment extends PathSegment {
	private final List<Object> columns;

	protected RowPathSegment(final List<Object> columns) {
		checkArgumentNotNull(columns, "columns");
		this.columns = columns;
	}
	
	/**
	 * Index columns make the row unique - we will use their values in the form [value-col-1, value-col-2, ...] as part of the property
	 * path
     * @param type the type description
     * @param row a row of the table
     * @return the path segment for the row
	 */
	public static RowPathSegment fromRow(final TabularType type, final CompositeData row) {
		final ArrayList<Object> columns = new ArrayList<Object>(type.getIndexNames().size());
		for (final String column : type.getIndexNames()) {
			columns.add(row.get(column));
		}
		return new RowPathSegment(columns);
	}

	@Override
	public String toString() {
		final StringBuilder result = new StringBuilder("[");
		for (final Object column : columns) {
			result.append(column.toString()).append(',');
		}
		result.delete(result.length()-1, result.length()).append(']');
		return result.toString();
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof RowPathSegment)) {
			return false;
		}
		final RowPathSegment that = (RowPathSegment) obj;
		return columns.equals(that.columns);
	}

	@Override
	public Object getValue(final Object value) {
		final TabularData table = (TabularData) value;
		for (final Object rowData : table.values()) {
			final CompositeData row = (CompositeData) rowData;
			final RowPathSegment rowSegment = RowPathSegment.fromRow(table.getTabularType(), row);
			if (this.equals(rowSegment)) {
				return row;
			}
		}
		// row not found!
		return null;
	}
}