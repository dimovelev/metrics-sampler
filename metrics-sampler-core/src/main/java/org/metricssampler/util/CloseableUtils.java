package org.metricssampler.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class CloseableUtils {
	private CloseableUtils() {
	}

	public static void closeQuietly(final Statement closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (final SQLException e) {
				// ignore
			}
		}
	}
	
	public static void closeQuietly(final ResultSet closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (final SQLException e) {
				// ignore
			}
		}
	}

}
