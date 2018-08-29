package de.unidue.ltl.ctest.io;

import java.io.File;

import org.junit.Test;

import de.unidue.ltl.ctest.core.CTestObject;
import de.unidue.ltl.ctest.io.CTestDocWriter;

public class CTestDocWriterTest {

	@Test
	public void docWriterTest() 
		throws Exception
	{
		File inputFile = new File("src/test/resources/texts/enTest.txt");
		CTestObject ctest = new CTestObject("en");
		ctest.initializeFromFile(inputFile);
		
		CTestDocWriter.write("cTest", "Französisch v1", new File("target/test.docx"), ctest, ctest);
	}
}
