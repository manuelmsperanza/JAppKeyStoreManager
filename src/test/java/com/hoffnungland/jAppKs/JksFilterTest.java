package com.hoffnungland.jAppKs;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

/**
 * Unit tests for {@link JksFilter}.
 */
public class JksFilterTest {

	/**
	 * Verifies that directories are accepted so users can navigate folders.
	 */
	@Test
	public void acceptShouldAllowDirectories() {
		JksFilter filter = new JksFilter();
		assertTrue(filter.accept(new File(".")));
	}

	/**
	 * Verifies that files ending in .jks are accepted.
	 */
	@Test
	public void acceptShouldAllowJksFiles() {
		JksFilter filter = new JksFilter();
		assertTrue(filter.accept(new File("keystore.jks")));
	}

	/**
	 * Verifies that non-keystore file extensions are rejected.
	 */
	@Test
	public void acceptShouldRejectNonJksFiles() {
		JksFilter filter = new JksFilter();
		assertFalse(filter.accept(new File("keystore.txt")));
	}
}
