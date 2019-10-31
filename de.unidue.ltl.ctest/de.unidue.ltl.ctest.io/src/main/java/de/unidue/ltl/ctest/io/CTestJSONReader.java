package de.unidue.ltl.ctest.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import de.unidue.ltl.ctest.core.CTestObject;
import de.unidue.ltl.ctest.util.Transformation;

/**
 * A class for reading JSON files, containing c-tests.
 */
public class CTestJSONReader implements CTestReader {
	@Override
	public CTestObject read(Path path) throws IOException {
		String json = Files.readString(path);
		return Transformation.fromJSONString(json);
	}

	@Override
	public CTestObject read(String filePath) throws IOException {
		return read(Paths.get(filePath));
	}

	@Override
	public CTestObject read(File file) throws IOException {
		return read(file.toPath());
	}
}
