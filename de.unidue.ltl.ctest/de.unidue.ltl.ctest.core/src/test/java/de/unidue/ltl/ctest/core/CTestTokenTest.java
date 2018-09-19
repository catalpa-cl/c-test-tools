package de.unidue.ltl.ctest.core;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;


public class CTestTokenTest {
	private CTestToken gapped;
	private CTestToken ungapped;
	
	@Before
	public void init() throws Exception {
		this.gapped = new CTestToken("gapped", "gap", 0.0, "alternative");
		this.gapped.setGapIndex(3);
		
		this.ungapped = new CTestToken("ungapped");
	}
	
	@Test
	public void testGapTypeSetting() {
		CTestToken token = gapped;
		
		assertEquals(GapType.POSTFIX, token.getGapType()); // default
		
		token.setGapType("foo"); // should not change gap type
		assertEquals(GapType.POSTFIX, token.getGapType());
		
		String prefixGap = GapType.PREFIX.toString();
		token.setGapType(prefixGap);
		assertEquals(GapType.PREFIX, token.getGapType());
	}
	
	@Test
	public void testPromptAndPrimarySolution() {
		String base = gapped.getText().substring(0, gapped.getGapIndex());
		String solution = gapped.getText().substring(gapped.getGapIndex());
		
		assertEquals(base, gapped.getPrompt());
		assertEquals(solution, gapped.getPrimarySolution());
		
		gapped.setPrompt(null);
		assertEquals(base, gapped.getPrompt()); // derived from CTesToken.text and CTestToken.gapIndex
		assertEquals(solution, gapped.getPrimarySolution());
		
		gapped.setGapIndex(-1);
		assertEquals("", gapped.getPrompt()); // entire word gapped
		assertEquals(gapped.getText(), gapped.getPrimarySolution()); // entire word is solution
	}
	
	@Test
	public void testSolutions() {
		List<String> solutions = Arrays.asList(new String[] { "ped", "alternative" });
		
		for (int i = 0; i < solutions.size(); i++)
			assertEquals(solutions.get(i), gapped.getAllSolutions().get(i));
	}
}
