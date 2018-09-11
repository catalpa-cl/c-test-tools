package de.unidue.ltl.ctest.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import de.unidue.ltl.ctest.core.CTestObject;
/**
 * Writer class for writing {@code CTestObject}s to file.
 * 
 * @see CTestObject
 */
public interface CTestWriter {
	/**
	 * Writes the the given {@code CTestObject} to the given {@code Path}.
	 * Existing file contents are overwritten.
	 * 
	 * @throws IOException if given path references a directory or writing fails otherwise.
	 */
	public void write(CTestObject ctest, Path path) throws IOException;
	/**
	 * Writes the the given {@code CTestObject} to the file under the given Path.
	 * Existing file contents are overwritten.
	 * 
	 * @throws IOException if given path references a directory or writing fails otherwise.
	 */
	public void write(CTestObject ctest, String filePath) throws IOException;
	/**
	 * Writes the the given {@code CTestObject} to the given {@code File}.
	 * Existing file contents are overwritten.
	 * 
	 * @throws IOException if given path references a directory or writing fails otherwise.
	 */
	public void write(CTestObject ctest, File file) throws IOException;
}
