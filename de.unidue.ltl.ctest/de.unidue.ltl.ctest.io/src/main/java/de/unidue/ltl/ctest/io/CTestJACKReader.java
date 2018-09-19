package de.unidue.ltl.ctest.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jsoup.Jsoup;

import de.unidue.ltl.ctest.core.CTestObject;
import de.unidue.ltl.ctest.core.CTestToken;

public class CTestJACKReader implements CTestReader {
	
	private static final String XML_GAP_MARKER = "(\\[fillIn groesse=\"\\d+\" parser=\".*?\"\\])";
	
	private static final String GAP_MARKER = "___GAP___";
	
	/**
	 * Regex, matching an encoded solution in the source xml File.
	 * <p>
	 * Capture group "gapId" captures the id of the gap.
	 * Capture group "solution" captures a solution of the gap.
	 */
	public static final Pattern SOLUTION = Pattern.compile("'\\[pos=(?<gapId>\\d+)\\]','(?<solution>\\p{L}+?)'");
	
	/**
	 * Regex, matching a gap in the source xml File.
	 */
	public static final Pattern XML_GAP = Pattern.compile(XML_GAP_MARKER);
	
	/**
	 * Regex, matching a gap in the extracted words.
	 */
	public static final Pattern GAP = Pattern.compile(GAP_MARKER);
	
	/**
	 * Regex matching punctuation and special characters, attached to ctest tokens.
	 * <p>
	 * It offers 3 capture groups:
	 * <ul>
	 * <li> {@code pre} captures non-text characters preceding the token.
	 * <li> {@code token} captures the token.
	 * <li> {@code post} capture non-text characters following the token.
	 * </ul>
	 */
	public static final Pattern JUNK = Pattern.compile(""
			+ "(?<pre>[^\\p{L}]*)"
			+ "(?<token>[\\p{L}]*(___GAP___)?)"
			+ "(?<post>[^\\p{L}]*)");
	
	/**
	 * Regex matching punctuation at the end of a sentence.
	 */
	public static final Pattern END_OF_SENTENCE = Pattern.compile("[!\\.\\?]");
	
	@Override
	public CTestObject read(Path path) throws IOException {
		Document document = readXMLDocument(path.toFile());
		
		List<List<String>> tokenSolutions = extractSolutions(document);
		String[] xmlLines = extractXMLLines(document);
		
		String title = extractTitle(xmlLines);		
		String[] words = extractWords(xmlLines);
		List<CTestToken> tokens = extractTokens(words, tokenSolutions);
		
		CTestObject ctest = new CTestObject("UNKNOWN");
		ctest.setId(title);
		tokens.forEach(ctest::addToken);
		return ctest;
	}

	@Override
	public CTestObject read(String filePath) throws IOException {
		return this.read(Paths.get(filePath));
	}

	@Override
	public CTestObject read(File file) throws IOException {
		return this.read(file.getAbsolutePath());
	}
	
	
	private Document readXMLDocument(File xmlFile) throws IOException {
		try {
			return new SAXBuilder().build(xmlFile);
		} catch (JDOMException e) { 
			System.err.println("Could not parse XML with JDOM SAXBuilder. File: " + xmlFile.getAbsolutePath());
			throw new IOException(e); 
		}
	}

	private String extractTitle(String[] xmlLines) {
		String xmlTitle = xmlLines[0].length() < 2 ? xmlLines[1] : xmlLines[0]; // Take second line if header is too short.
		return Jsoup.parse(xmlTitle)
				.text()
				.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
	}

	private String[] extractXMLLines(Document document) {
		return document
				.getRootElement()
				.getChild("task")
				.getValue()
				.split("\n");
	}
	
	private String[] extractWords(String[] xmlLines) {
		String xmlDoc = String.join("", Arrays.copyOfRange(xmlLines, 1, xmlLines.length)); // Skip title line	
		return Jsoup.parse(xmlDoc)
				.text()
				.replaceAll(XML_GAP_MARKER, GAP_MARKER)
				.split(" ");
	}
	
	private List<List<String>> extractSolutions(Document document) throws IOException {		
		List<Element> options = document
				.getRootElement()
				.getChild("feedback")
				.getChildren("option");
		
		List<List<String>> solutions = options.stream()
				.map(option -> new ArrayList<String>())
				.collect(Collectors.toList());
		solutions.add(new ArrayList<>()); // since gaps are 1 indexed, 1 additional field is (sometimes) required.
		
		Matcher solutionMatcher = SOLUTION.matcher("");
		
		for(Element e : options){
			String solutionDescription = e.getAttributeValue("result");
			
			solutionMatcher.reset(solutionDescription);
			if (!solutionMatcher.find()) {
				new IOException ("XML Element malformed: " + solutionDescription).printStackTrace();
				continue;
			}
			
			int gapId = Integer.parseInt(solutionMatcher.group("gapId"));
			String solution = solutionMatcher.group("solution");
			
			solutions.get(gapId).add(solution);
		}
		
		return solutions;
	}
	
	private List<CTestToken> extractTokens(String[] words, List<List<String>> tokenSolutions) {
		List<CTestToken> tokens = new ArrayList<>();
		
		Matcher gapMatcher = GAP.matcher("");
		Matcher junkMatcher = JUNK.matcher("");
		
		int gapId = 1;
		
		for(String word : words){
			String pre = "";
			String post = "";
			
			
			junkMatcher.reset(word);
			// find attached punctuation
			if(junkMatcher.matches() && word.length() > 1) {
				pre = junkMatcher.group("pre");
				word = junkMatcher.group("token");
				post = junkMatcher.group("post");
			}
			
			gapMatcher.reset(word);
			CTestToken token = new CTestToken(word);
			
			// Add specific information for gapped tokens
			if(gapMatcher.find()) {
				int gapIndex = gapMatcher.start();
				String stem = word.substring(0, gapIndex);
				List<String> solutions = tokenSolutions.get(gapId);
				String text = stem + solutions.get(0);
				List<String> otherSolutions = solutions.subList(1, solutions.size()); // All solutions except the first.
				
				token.setText(text);
				token.setGap(true);
				token.setGapIndex(gapIndex);
				token.setPrompt(stem);
				token.setOtherSolutions(otherSolutions);
				token.setId(Integer.toString(gapId));
				
				gapId++;
			}
			if(!pre.isEmpty()) tokens.add(new CTestToken(pre));
			tokens.add(token);
			if(!post.isEmpty()) tokens.add(new CTestToken(post));
		}
		
		// Set last tokens in sentence
		Matcher sentenceEnd = END_OF_SENTENCE.matcher("");
		for (CTestToken t : tokens) {
			sentenceEnd.reset(t.getText());
			t.setLastTokenInSentence(sentenceEnd.find());
		}
		
		return tokens;
	}
}
