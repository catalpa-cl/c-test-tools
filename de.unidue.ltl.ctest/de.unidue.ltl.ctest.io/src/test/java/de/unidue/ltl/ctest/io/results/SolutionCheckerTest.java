package de.unidue.ltl.ctest.io.results;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;

import de.unidue.ltl.ctest.core.CTestObject;
import de.unidue.ltl.ctest.core.CTestToken;
import de.unidue.ltl.ctest.io.CTestJACKReader;
import junit.framework.TestCase;

public class SolutionCheckerTest extends TestCase {

	@Test
	public void testSolutionChecker() throws IOException {
		CTestObject ctest = new CTestJACKReader().read(new File("src/test/resources/texts/ios/JACK/TEST_2/stage1.xml"));
		int id = 4;
		CTestToken token = ctest.getGappedTokens().get(id);
		List<String> answers = new JACKSolutionReader().read("src/test/resources/texts/ios/JACK/TEST_2_solution/1/");
		SolutionChecker checker = new SolutionChecker();
		checker.set(ctest);
		
		assertNull(token.getErrorRate());
		
		checker.addSolutions(answers);
		checker.applyTestResults();
		
		assertNotNull(token.getErrorRate());
		assertEquals(0.0, token.getErrorRate());
		
		checker.addSolution(id, "CORRECT");
		checker.addSolution(id, "WRONG");
		checker.addSolution(id, "WRONGER");
		checker.addSolution(id, "WRONGEREST");
		
		assertEquals(0.0, token.getErrorRate());
		
		checker.applyTestResults();
		
		assertEquals(0.6, token.getErrorRate());
				
	}
}
