package de.unidue.ltl.ctest.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import de.unidue.ltl.ctest.core.CTestObject;

public interface CTestWriter {
	public void write(CTestObject object, Path filePath) throws IOException;
	public void write(CTestObject object, String filePath) throws IOException;
	public void write(CTestObject object, File file) throws IOException;
}
