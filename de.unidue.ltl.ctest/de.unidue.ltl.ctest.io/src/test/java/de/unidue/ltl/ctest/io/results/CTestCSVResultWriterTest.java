package de.unidue.ltl.ctest.io.results;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;

import de.unidue.ltl.ctest.core.CTestObject;
import de.unidue.ltl.ctest.core.CTestToken;
import de.unidue.ltl.ctest.io.CTestJACKReader;
import junit.framework.TestCase;

public class CTestCSVResultWriterTest extends TestCase {
	
	@Test
	public void testSolutionChecker() throws IOException {
		CTestObject ctest = new CTestJACKReader().read(new File("src/test/resources/texts/ios/JACK/TEST_2/stage1.xml"));
		int id = 4;
		CTestToken token = ctest.getGappedTokens().get(id);
		List<List<String>> answers = new JACKSolutionReader().readAll("src/test/resources/texts/ios/JACK/TEST_2_solution/");
		SolutionChecker checker = new SolutionChecker();
		checker.set(ctest);
		
		answers.forEach(answer -> checker.addSolutions(answer));
		
		List<TokenTestResult> results = checker.getTestResults();
		
		File out = new File("src/test/resources/temp/TEST_2.csv");
		CTestResultWriter writer = new CTestCSVResultWriter();
		writer.write(ctest, results, out);
	}
	
}
