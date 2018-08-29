package de.unidue.ltl.ctest.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class CTestTest {
	
	@Rule
	public TemporaryFolder folder= new TemporaryFolder();
	
	//FIXME: Failing
	//TODO: Move to gapscheme.io
	@Test
    public void roundtripTest()
        throws Exception
    {
		File inputFile = new File("src/test/resources/texts/enTest.txt");
		CTestObject ctest = new CTestObject("en");
		ctest.initializeFromFile(inputFile);

	    File outputFile = folder.newFile("output.txt");
	    FileUtils.writeStringToFile(outputFile, ctest.toString());
	    
	    // try to read in the created file
	    CTestObject ctestRoundtrip = new CTestObject("en");
	    ctestRoundtrip.initializeFromFile(outputFile);

		assertEquals(ctest.toString(), ctestRoundtrip.toString());
    }
	
	@Test
    public void predictionTest()
        throws Exception
    {
		File inputFile = new File("src/test/resources/texts/enTest.txt");
		CTestObject ctest = new CTestObject("en");
		ctest.initializeFromFile(inputFile);

		double p = 0.0;
		for(int i = 0; i<20; i++){
			assertTrue(ctest.setPrediction(p, i));
			p += 1.0;
		}

		// TODO test something
    }
}
