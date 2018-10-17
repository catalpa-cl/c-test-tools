package de.unidue.ltl.ctest.gapscheme;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.unidue.ltl.ctest.core.CTestObject;
import de.unidue.ltl.ctest.core.CTestToken;
import de.unidue.ltl.ctest.gapscheme.preprocessing.GapIndexFinder;

/**
 * Builder class for {@link CTestObject} objects.
 */
public class CTestGenerator {	
	private List<Predicate<Token>> exclusionRules;
	private List<GapIndexFinder> gapIndexFinders;
	private List<Sentence> sentences;
	private List<String> warnings;
	
	private CTestObject ctest;
	private JCas jcas;
	private String language;
	private String text;

	private int gapInterval;
	private int gapLimit;
	
	private int gapCandidates;
	private int gapCount;
	private int sentenceCount;
	private int sentenceLimit;
	
	
	/**
	 * Creates a new {@code CTestGenerator}.
	 * <p>
	 * By default, a c-test will contain 20 gaps, every second being gapped.
	 */
	public CTestGenerator() {
		gapInterval = 2;
		gapLimit = 20;
	}
	

	/**
	 * Creates a new {@code CTestGenerator} with the specified gap limit and interval between gaps.
	 * 
	 * @param  gapLimit The maximum number of gaps in the {@code CTestGenerator}. 
	 * @param  gapInterval The interval in which gaps are placed. If the interval is n, every nth candidate word will receive a gap.
	 */
	public CTestGenerator(int gapLimit, int gapInterval) {
		this.gapLimit = gapLimit;
		this.gapInterval = gapInterval;
	}
	
	/**
	 * Generates a {@code CTestObject} from the given text and the given language code.
	 * <p>
	 * The {@code language} parameter determines which language-specific exclusion criteria and gapping rules are applied to candidate words. 
	 * If the language is not supported, language independent criteria and rules are applied.
	 * 
	 * @param  text The text to be converted to a c-test. Should contain at least three sentences.
	 * @param  language The language of the text. Should be a ISO 639 two-letter language code.
	 * @return The generated {@code CTestObject}.
	 * 
	 * @throws UIMAException if UIMA preprocessing resources could not be initialised.
	 * 
	 * @see CTestResourceProvider#getSupportedLanguages()
	 * @see CTestObject
	 */
	public CTestObject generateCTest(String text, String language) throws UIMAException {
		initialise(text, language);
		makeGaps();
		generateWarnings();
		return ctest;
	}
	
	/**
	 * Generates a {@code CTestObject} from the given input text, ignoring any C-Test constraints.
	 * <p>
	 * The C-Test is generated as usual, with the exception that the first and last sentences are
	 * also gapped and there is no limit to the number of gaps in the C-Test. Consequently, no
	 * warnings are generated.
	 * Note that the <b><i>first</i></b> token in the text is gapped, unlike in normal gapschemes,
	 * where the second token receives the first gap.
	 */
	public CTestObject generatePartialCTest(String text, String language) throws UIMAException {
		initialise(text, language);
		makeSimpleGaps();
		return ctest;
	}
			
	/**
	 * Generates the C-Test.
	 * <p>
	 * The given text is gapped, according to the normal gapping rules, 
	 * starting at the <b><i>first</i></b> token in the text. 
	 */
	//FIXME: Not working
	private void makeSimpleGaps() {
		ctest = new CTestObject(language);
		sentences = new ArrayList<>(JCasUtil.select(jcas, Sentence.class));

		gapCandidates = 0;
		
		for (Sentence sentence : sentences) {
			for (Token token : JCasUtil.selectCovered(jcas, Token.class, sentence)) {
				CTestToken cToken = new CTestToken(token.getCoveredText());
				cToken.setGapIndex(estimateGapIndex(token));
				
				// check if exclusion rule applies
				boolean exclude = false;
				for (Predicate<Token> rule : exclusionRules)
					if (rule.test(token)) {
						exclude = true;
						break;
					}
						
				if (!exclude) {
					if (gapCandidates % gapInterval == 0) // first token is gapped
						cToken.setGap(true);
					gapCandidates++;
				}
				ctest.addToken(cToken);
			}
			sentenceCount++;
		}
	}


	/**
	 * Returns the last <b><i>successfully</i></b> generated {@code CTestObject}.
	 */	
	public CTestObject getCTest() {
		return ctest;
	}
	
	/**
	 * Returns the number of gaps in the last <b><i>successfully</i></b> generated {@code CTestObject}.
	 */	
	public int getGapCount() {
		return ctest.getGapCount();
	}
	
	/**
	 * Returns the interval of gaps for generated c-tests.
	 * <p>
	 * The interval applies only to valid candidate words. Invalid words, such as names are not regarded in the interval. 
	 */	
	public int getGapInterval() {
		return gapInterval;
	}

	/**
	 * Returns the maximum number of gaps for generated c-tests.
	 */	
	public int getGapLimit() {
		return gapLimit;
	}
	
	/**
	 * Returns the language of the c-test. See the {@code generateCTest} method for details on the role of the language.
	 * 
	 * @see CTestGenerator#generateCTest(String, String)
	 */	
	public String getLanguage() {
		return language;
	}
	
	/**
	 * Returns the source text of the c-test.
	 */	
	public String getText() {
		return text;
	}
	
	/**
	 * Returns a list of warnings that apply to the last <b><i>successful</i></b> run of <code>generateCTest<code>.
	 */	
	public List<String> getWarnings() {
		return warnings;
	}
	
	/**
	 * Processes the text and initialises exclusion criteria and gap index finders, based on the passed language.
	 */
	private void initialise(String aText, String aLanguage) throws UIMAException {
		language = aLanguage;
		text = aText;
		
		List<AnalysisEngine> engines = CTestResourceProvider.getAnalysisEngines(language);
		
		jcas = process(aText, aLanguage, engines);
		exclusionRules = CTestResourceProvider.getExclusionRules(jcas, language);
		gapIndexFinders = CTestResourceProvider.getGapFinders(jcas, language);
	}

	private JCas process(String aText, String aLanguage, List<AnalysisEngine> engines) throws UIMAException {
		JCas jcas = JCasFactory.createText(aText, aLanguage);
		for (AnalysisEngine engine : engines)
			engine.process(jcas);
		return jcas;
	}
	
	/**
	 * Creates the C-Test.
	 * <p>
	 * For each Token in the text, it is first tested, whether the token is a candidate for gapping, then a corresponding CTestToken is generated.
	 * For valid candidates, the index of the gap is determined using the estimateGapIndex method.
	 */
	private void makeGaps() {		
		ctest = new CTestObject(language);
		sentences = new ArrayList<>(JCasUtil.select(jcas, Sentence.class));

		sentenceCount = 0;
		sentenceLimit = sentences.size() - 1;
		gapCount = 0;
		gapCandidates = 0;
		
		for (Sentence sentence : sentences) {
			for (Token token : JCasUtil.selectCovered(jcas, Token.class, sentence)) {
				CTestToken cToken = new CTestToken(token.getCoveredText());
				cToken.setGapIndex(estimateGapIndex(token));
				
				if(isValidGapCandidate(token)) {
					if (gapCandidates % gapInterval != 0) {
						cToken.setGap(true);
						gapCount++;
						if (gapCount == gapLimit)
							sentenceLimit = sentences.indexOf(sentence) + 1;
					}
					gapCandidates++;
				}
				ctest.addToken(cToken);
			}
			sentenceCount++;
		}
	}
	
	/**
	 * Checks whether the given Token is eligible for gapping.
	 */
	private boolean isValidGapCandidate(Token token) {
		if (sentenceCount == 0)
			return false;

		if (gapCount == gapLimit)
			return false;

		for (Predicate<Token> criterion : exclusionRules) {
			if (criterion.test(token))
				return false;
		}

		return true;
	}

	/** 
	 * Estimates the index at which a gap should be placed for the given Token. 
	 * 
	 * Suggestions for gap indices are generated by the CTestGenerator's gapIndexFinders. 
	 * The index closest to the end of the word is chosen. 
	 * Then the gap position is determined, using the default gapping rule (half of the word). 
	 * The generated index assumes that gaps should be placed at the end of the word (playgro___ instead of __ayground for "playground"). 
	 */
	protected int estimateGapIndex(Token token) {
		int gapRangeStart = 0;

		for (GapIndexFinder modifier : gapIndexFinders) {
			if (modifier.test(token)) {
				gapRangeStart = Math.max(gapRangeStart, modifier.getGapIndex(token));
			}
		}

		int gapOffset = token.getCoveredText().substring(gapRangeStart).length() / 2;

		return gapRangeStart + gapOffset;
	}
	
	/**
	 * Adds warnings for the last run. Their primary purpose is to inform the user that the quality of the c-test may be less than optimal.
	 */
	private void generateWarnings() {
		warnings = new ArrayList<>();
		
		if (sentenceCount < sentenceLimit || sentences.size() < 3) {
			warnings.add(
					"INSUFFICIENT NUMBER OF SENTENCES - The supplied text did not contain enough sentences."
					+ " You may need to add additional sentences.");
		}

		//FIXME #3: Also generated, when sentence limit is below sentence count.
		if (sentenceCount > sentenceLimit) {
			warnings.add(
					"TOO MANY SENTENCES - The supplied text contained more sentences than necessary. "
					+ "The c-test did not use all sentences.");
		}
		
		if (gapCount < gapLimit) {
			warnings.add(String.format(
					"INSUFFICIENT NUMBER OF GAPS - The supplied text was too short to produce at least %s gaps. "
					+ "Try to add more words.", gapLimit));
		}
	}
}
