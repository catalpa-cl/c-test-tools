package de.unidue.ltl.ctest.io.results;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import de.unidue.ltl.ctest.core.CTestObject;

/**
 * A writer class for writing the results of a C-Test to file.
 * <p>
 * A <i>result</i> of a C-Test is the sum of all solutions for all gapped tokens in the C-Test.
 */
public interface CTestResultWriter {
	
	public void write(CTestObject ctest, List<TokenTestResult> results, Path path) throws IOException;
	
	public void write(CTestObject ctest, List<TokenTestResult> results, File file) throws IOException;
	
	public void write(CTestObject ctest, List<TokenTestResult> results, String filePath) throws IOException;
	
}
