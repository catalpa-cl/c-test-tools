package de.unidue.ltl.ctest.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import de.unidue.ltl.ctest.core.CTestObject;
import de.unidue.ltl.ctest.core.CTestToken;

public class CTestIOSReader implements CTestReader {
	/**
	 * Regex matching gapped ctest tokens and their constituent parts, using capture groups. 
	 * <p>
	 * The constitutents are identified in different capture groups:
	 * <ul>
	 * <li> {@code wordBase} captures the part of the token that is not gapped.
	 * <li> {@code solutions} captures the solutions to the token.
	 * </ul>
	 *   
	 * @see Matcher
	 */
	public static final Pattern TOKEN = Pattern.compile(""
			+ "(?<wordBase>\\p{L}*?)"
			+ "\\{(?<solutions>.*?)\\}");
	
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
			+ "(?<text>[\\p{L}\\{,\\}]*)"
			+ "(?<post>[^\\p{L}]*)");
	
	/**
	 * Regex matching punctuation at the end of a sentence.
	 */
	public static final Pattern END_OF_SENTENCE = Pattern.compile("[!\\.\\?]");
	
	/**
	 *  Pattern matching curly braces.
	 */
	public static final Pattern CURLY_BRACES = Pattern.compile("[\\{\\}]");
	
	@Override
	public CTestObject read(Path path) throws IOException {
		String language = extractLanguage(path);
		List<CTestToken> tokens = extractTokens(path);

		CTestObject object = new CTestObject(language);
		tokens.forEach(object::addToken);
		return object;
	}

	@Override
	public CTestObject read(String filePath) throws IOException {
		return this.read(Paths.get(filePath));
	}

	@Override
	public CTestObject read(File file) throws IOException {
		return this.read(Paths.get(file.getAbsolutePath()));
	}

	private String extractLanguage(Path path) {
		path = path.toAbsolutePath();
		if (path.getNameCount() < 2)
			return "UNKNOWN";

		// TODO: Validate language.
		return path.subpath(path.getNameCount() - 2, path.getNameCount() - 1).toString();
	}

	// TODO: Implement in a cleaner fashion.
	private List<CTestToken> extractTokens(Path path) throws IOException {
		List<CTestToken> tokens = new ArrayList<>();
		String[] words = Files.lines(path)
				.map(line -> line.trim())
				.collect(Collectors.joining(" "))
				.split(" ");
				
		Matcher junkMatcher = JUNK.matcher("");
		Matcher tokenMatcher = TOKEN.matcher("");
		Matcher curlyBracesMatcher = CURLY_BRACES.matcher("");

		int currentID = 0;
		
		for (String word : words) {			
			String pre = "";
			String post = "";
			
			// Checks whether word is clean. 
			junkMatcher.reset(word);
			if(junkMatcher.find() && word.length() > 1) {
				pre = junkMatcher.group("pre");
				word = junkMatcher.group("text");
				post = junkMatcher.group("post");
			}
			
			CTestToken token = new CTestToken(word);
			tokenMatcher.reset(word);
			
			// Adds additional information, if word is a gapped token.
			if (tokenMatcher.find()) {
				String wordBase = tokenMatcher.group("wordBase");
				String[] solutions = tokenMatcher.group("solutions").split(",");
				String text = wordBase + solutions[0];
				List<String> otherSolutions = Arrays.asList(solutions).subList(1, solutions.length);
				int gapIndex = word.indexOf("{");
				
				token.setText(text);
				token.setGap(true);
				token.setGapIndex(gapIndex);
				token.setPrompt(wordBase);
				token.setOtherSolutions(otherSolutions);
				token.setId(Integer.toString(currentID));			
				currentID++;
				
				curlyBracesMatcher.reset(token.getText());
				if (curlyBracesMatcher.find()) {
					System.err.println("Malformed input text!" + token.getText());
					throw new IOException();
				}				
			}
			
			if (!pre.isEmpty()) tokens.add(new CTestToken(pre));
			tokens.add(token);
			if (!post.isEmpty()) tokens.add(new CTestToken(post));			
		}
		
		// Set last tokens in sentence
		Matcher sentenceEnd = END_OF_SENTENCE.matcher("");
		for (CTestToken t : tokens) {
			sentenceEnd.reset(t.getText());
			t.setLastTokenInSentence(sentenceEnd.matches());
		}
		
		return tokens;
	}
}
