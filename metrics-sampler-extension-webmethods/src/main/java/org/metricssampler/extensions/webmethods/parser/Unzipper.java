package org.metricssampler.extensions.webmethods.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class Unzipper {
	private static final Logger logger = LoggerFactory.getLogger(Unzipper.class);
	private final File file;
	private final long maxEntrySize;
	private ZipFile zip;

	public Unzipper(final File file, final long maxEntrySize) {
		this.file = file;
		this.maxEntrySize = maxEntrySize;
	}

	protected ZipFile getZip() throws ZipException, IOException {
		if (zip == null) {
			zip = new ZipFile(file);
		}
		return zip;
	}

	public List<String> getEntries() throws ZipException, IOException {
		final List<String> result = new LinkedList<String>();
		final Enumeration<? extends ZipEntry> entries = getZip().entries();
		while (entries.hasMoreElements()) {
			final ZipEntry entry = entries.nextElement();
			final long entrySize = entry.getSize();
			if (entrySize <= maxEntrySize) {
				result.add(entry.getName());
			}
		}
		return result;
	}
	public InputStream unzip(final String name) throws ZipException, IOException {
		final ZipFile zip = getZip();
		final ZipEntry entry = zip.getEntry(name);
		if (entry != null) {
			final long entrySize = entry.getSize();
			if (entrySize <= maxEntrySize) {
				logger.debug("Unzipping \"{}\" from \"{}\"", name, file);
				return zip.getInputStream(entry);
			} else {
				logger.warn("The zip entry \"" + name + "\" is larger than the maximal entry size and will be skipped: " + entrySize + " > " + maxEntrySize);
			}
		} else {
			logger.warn("Zip file \"{}\" did not contain entry named \"{}\"", file, name);
		}
		return null;
	}

	public void close() throws IOException {
		zip.close();
		zip = null;
	}
}
