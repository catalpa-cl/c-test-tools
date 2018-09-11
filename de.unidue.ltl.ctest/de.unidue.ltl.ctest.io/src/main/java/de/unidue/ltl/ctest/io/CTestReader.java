package de.unidue.ltl.ctest.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import de.unidue.ltl.ctest.core.CTestObject;

/**
 * Reader class for reading {@code CTestObject}s from file.
 * 
 * @see CTestObject
 */
public interface CTestReader {
	/**
	 * Reads the file under the given {@code Path} and returns a {@code CTestObject}.
	 * 
	 * @throws IOException if file does not exist or reading fails otherwise.
	 */
	public CTestObject read(Path path) throws IOException;	
	/**
	 * Reads the given {@code File} and returns a {@code CTestObject}.
	 * 
	 * @throws IOException if file does not exist or reading fails otherwise.
	 */
	public CTestObject read(String filePath) throws IOException;
	/**
	 * Reads the file under the given path and returns a {@code CTestObject}.
	 * 
	 * @throws IOException if file does not exist or reading fails otherwise.
	 */
	public CTestObject read(File file) throws IOException;
}
