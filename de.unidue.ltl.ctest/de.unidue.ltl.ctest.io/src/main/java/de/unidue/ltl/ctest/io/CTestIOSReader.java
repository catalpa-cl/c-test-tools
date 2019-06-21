package de.unidue.ltl.ctest.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import de.unidue.ltl.ctest.core.CTestObject;
import de.unidue.ltl.ctest.core.CTestToken;
import de.unidue.ltl.ctest.util.IOSModelVersion;

public class CTestIOSReader implements CTestReader {
	/**
	 * Regex matching gapped ctest tokens and their constituent parts, using capture groups. 
	 * <p>
	 * The constitutents are identified in different capture groups:
	 * <ul>
	 * <li> {@code base} captures the part of the token that is not gapped.
	 * <li> {@code solutions} captures the solutions to the token.
	 * </ul>
	 *   
	 * @see Matcher
	 */
	public static final Pattern TOKEN_V1 = Pattern.compile(""
			+ "(?<base>\\p{L}*?)"
			+ "\\{(?<solutions>.*?)\\}");
	
	/**
	 * Regex matching gapped ctest tokens and their constituent parts, using capture groups. 
	 * <p>
	 * The constitutents are identified in different capture groups:
	 * <ul>
	 * <li> {@code base} captures the part of the token that is not gapped.
	 * <li> {@code solution} captures the primary solutions to the token.
	 * <li> {@code solutions} captures alternate solutions to the token.
	 * </ul>
	 *   
	 * @see Matcher
	 */
	public static final Pattern TOKEN_V2 = Pattern.compile(""
			+ "(?<base>\\p{L}*?)"
			+ "\\[(?<solution>.*?)\\]"
			+ "( ?\\{(?<solutions>.*?)\\})?");
	
	
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
	public static final Pattern JUNK_V1 = Pattern.compile(""
			+ "(?<pre>[^\\p{L}]*)"
			+ "(?<token>[\\p{L}\\{,; \\}]*)"
			+ "(?<post>[^\\p{L}]*)");
	
	public static final Pattern JUNK_V2 = Pattern.compile(""
			+ "(?<pre>[^\\p{L}]*)"
			+ "(?<token>[\\p{L}\\[,; \\]\\{\\}]*)"
			+ "(?<post>[^\\p{L}]*)");
	
	
	/**
	 * Regex matching punctuation at the end of a sentence.
	 */
	public static final Pattern END_OF_SENTENCE = Pattern.compile("[!\\.\\?]");
	
	/**
	 *  Pattern matching curly braces.
	 */
	public static final Pattern CURLY_BRACES = Pattern.compile("[\\{\\}]");
	
	public static final String DEFAULT_DELIMITER = ",";
	
	public static Pattern getTokenPattern(IOSModelVersion version) {
		switch(version) {
			case V1: return TOKEN_V1;
			default: return TOKEN_V2;
		}
	}
	
	public static Pattern getJunkPattern(IOSModelVersion version) {
		switch(version) {
			case V1: return JUNK_V1;
			default: return JUNK_V2;
		}
	}
	
	public String delimiter;
	public IOSModelVersion version;
	
	private Matcher junkMatcher;
	private Matcher tokenMatcher;
	private Matcher curlyBracesMatcher;
	
	public CTestIOSReader() {
		this.delimiter = DEFAULT_DELIMITER;
		this.version = IOSModelVersion.CURRENT;
	}
	
	public CTestIOSReader(String delimiter) {
		this();
		this.delimiter = delimiter;
	}

	public CTestIOSReader(IOSModelVersion version) {
		this();
		this.version = version;
	}

	
	public CTestIOSReader(String delimiter, IOSModelVersion version) {
		this.delimiter = delimiter;
		this.version = version;
	}
	
	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public IOSModelVersion getVersion() {
		return version;
	}

	public void setVersion(IOSModelVersion version) {
		this.version = version;
	}

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
		// Has no parent.
		if (path.getNameCount() < 2) 
			return "UNKNOWN";

		// TODO: validate language
		// Extracts language from folder structure. Parent folder is language.
		return path.getParent().getFileName().toString();
	}
	
	private List<CTestToken> extractTokens(Path path) throws IOException {
		List<CTestToken> tokens = new ArrayList<>();
		
		// Read file.
		String text = Files.lines(path)
				.map(line -> line.trim())
				.collect(Collectors.joining(" "));
		
		// Remove spaces from tokens.
		tokenMatcher = getTokenPattern(this.version).matcher(text);
		StringBuilder cleaned = new StringBuilder();
		while(tokenMatcher.find()) {
			String tokenText = tokenMatcher.group();
			tokenMatcher.appendReplacement(cleaned, tokenText.replaceAll(" ", ""));
		}
		tokenMatcher.appendTail(cleaned);
		text = cleaned.toString();
		
		String[] words = text.split(" ");
		
		junkMatcher = getJunkPattern(version).matcher("");
		curlyBracesMatcher = CURLY_BRACES.matcher("");
		
		int currentID = 0;
		
		for (String word : words) {			
			
			String pre = "";
			String post = "";
			
			// Check if word is clean or contains junk. 
			junkMatcher.reset(word);
			if(junkMatcher.find() && word.length() > 1) {
				pre = junkMatcher.group("pre");
				word = junkMatcher.group("token");
				post = junkMatcher.group("post");
			}
			
			CTestToken token = extractToken(word);
			token.setId(Integer.toString(currentID));			
			currentID++; // FIXME: breaks read -> write -> read equality. Should only increase for gapped tokens. 
			
			// Check if token text contains a curly brace...
			curlyBracesMatcher.reset(token.getText());
			if (curlyBracesMatcher.find()) {
				System.err.println("Malformed input text!" + token.getText());
				throw new IOException();
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
	
	private CTestToken extractToken(String word) {		
		CTestToken token = new CTestToken(word);
		
		// Add additional information, if word is a gapped token.
		tokenMatcher.reset(word);
		if (tokenMatcher.find()) {
			switch(version) {
				case V1: {
					String[] solutions = tokenMatcher.group("solutions").split(delimiter);
					String base = tokenMatcher.group("base").trim();
					String text = base + solutions[0].trim();
					List<String> otherSolutions = Arrays.stream(solutions)
							.skip(1)
							.map(String::trim)
							.collect(Collectors.toList());
					int gapIndex = word.indexOf("{");
					
					token.setText(text);
					token.setGap(true);
					token.setGapIndex(gapIndex);
					token.setPrompt(base);
					token.setOtherSolutions(otherSolutions);	
					break;
				}
				case V2: ;
				default: {
					int gapIndex = word.indexOf("["); // solution marker
					String base = tokenMatcher.group("base").trim();
					String solution = tokenMatcher.group("solution");
					List<String> solutions = new ArrayList<>();
					
					String otherSolutions = tokenMatcher.group("solutions");
					if (otherSolutions != null) {
						solutions = Arrays.stream(otherSolutions.split(delimiter))
								.map(alternative -> alternative.trim().substring(gapIndex))
								.collect(Collectors.toList());
					}
					
					String text = base + solution.trim();
					
					token.setText(text);
					token.setGap(true);
					token.setGapIndex(gapIndex);
					token.setPrompt(base);
					token.setOtherSolutions(solutions);
				}				
			}					
		}
		
		return token;
	}
}
