package de.unidue.ltl.ctest.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import de.unidue.ltl.ctest.core.CTestObject;

public interface CTestReader {
	public CTestObject read(Path path) throws IOException;	
	public CTestObject read(String filePath) throws IOException;
	public CTestObject read(File file) throws IOException;
}
