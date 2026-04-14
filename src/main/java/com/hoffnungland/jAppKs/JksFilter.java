package com.hoffnungland.jAppKs;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Filters file chooser entries to Java KeyStore files.
 */
public class JksFilter extends FileFilter {
	
	private static final Logger logger = LogManager.getLogger(JksFilter.class);

	/**
	 * Accepts directories and files with a {@code .jks} extension.
	 *
	 * @param f candidate file from the chooser
	 * @return {@code true} when the candidate is a directory or a JKS file
	 */
	@Override
	public boolean accept(File f) {
		logger.traceEntry();
		if (f.isDirectory()) {
	        return logger.traceExit(true);
	    }
		
		String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
            if(ext.equalsIgnoreCase("jks")) {
            	return logger.traceExit(true);
            }
        }
		return logger.traceExit(false);
	}

	/**
	 * Returns the user-visible description for this filter.
	 *
	 * @return filter description
	 */
	@Override
	public String getDescription() {
		return "JKS *.jks";
	}

}
